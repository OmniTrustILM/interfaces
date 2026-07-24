package com.otilm.api.clients.v2;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.otilm.api.clients.BaseApiClient;
import com.otilm.api.exception.ConnectorProblemException;
import com.otilm.api.model.client.attribute.RequestAttributeV2;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v2.content.StringAttributeContentV2;
import com.otilm.api.model.common.error.ErrorCode;
import com.otilm.api.model.connector.cryptography.v2.token.TokenScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenStatusV2;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.model.core.connector.ConnectorStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.*;

class TokenApiClientTest {
    private TokenApiClient client;
    private ConnectorDto connector;
    private WireMockServer server;

    @BeforeEach
    void setUp() {
        client = new TokenApiClient(BaseApiClient.prepareWebClient(), null);
        server = new WireMockServer(options().dynamicPort());
        server.start();
        WireMock.configureFor("localhost", server.port());
        connector = new ConnectorDto();
        connector.setUrl("http://localhost:" + server.port());
        connector.setStatus(ConnectorStatus.CONNECTED);
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void invokesAllReviewedTokenRoutes() throws Exception {
        server.stubFor(WireMock.get("/v2/cryptographyProvider/tokens/attributes")
                .willReturn(WireMock.okJson("[]")));
        server.stubFor(WireMock.post("/v2/cryptographyProvider/tokens/status")
                .willReturn(WireMock.okJson("{\"status\":\"Connected\"}")));
        server.stubFor(WireMock.post("/v2/cryptographyProvider/tokens/tokenProfile/attributes")
                .willReturn(WireMock.okJson("[]")));
        server.stubFor(WireMock.post("/v2/cryptographyProvider/tokens/tokenProfile/keyUsages")
                .willReturn(WireMock.okJson("[\"sign\",\"unwrap\"]")));

        assertTrue(client.listTokenAttributes(connector).isEmpty());
        TokenScopedRequestV2Dto status = new TokenScopedRequestV2Dto();
        status.setTokenAttributes(List.of());
        assertEquals(TokenStatusV2.CONNECTED, client.getTokenStatus(connector, status).getStatus());
        TokenScopedRequestV2Dto profile = new TokenScopedRequestV2Dto();
        profile.setTokenAttributes(List.of());
        assertTrue(client.listTokenProfileAttributes(connector, profile).isEmpty());
        assertEquals(2, client.listTokenProfileKeyUsages(connector, profile).size());
    }

    @Test
    void statusStructuredFailureIsPreservedWithoutRequestContent() {
        String problemJson = """
                {
                  "type": "https://docs.otilm.com/problems/common/SERVICE_UNAVAILABLE",
                  "title": "Service unavailable",
                  "status": 503,
                  "detail": "Token provider is temporarily unavailable",
                  "errorCode": "SERVICE_UNAVAILABLE",
                  "timestamp": "2026-07-23T10:00:00Z",
                  "correlationId": "token-test-correlation",
                  "retryable": true,
                  "retryAfterSeconds": 30
                }
                """;
        server.stubFor(WireMock.post("/v2/cryptographyProvider/tokens/status")
                .willReturn(WireMock.aResponse()
                        .withStatus(503)
                        .withHeader("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                        .withBody(problemJson)));
        TokenScopedRequestV2Dto request = new TokenScopedRequestV2Dto();
        String secret = "expanded-pin-1234";
        request.setTokenAttributes(List.of(new RequestAttributeV2(UUID.randomUUID(), "pin",
                AttributeContentType.STRING, List.of(new StringAttributeContentV2(secret)))));
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        ListAppender<ILoggingEvent> captured = new ListAppender<>();
        captured.start();
        root.addAppender(captured);

        try {
            ConnectorProblemException exception = assertThrows(ConnectorProblemException.class,
                    () -> client.getTokenStatus(connector, request));
            assertEquals(ErrorCode.SERVICE_UNAVAILABLE, exception.getProblemDetail().getErrorCode());
            assertTrue(exception.getProblemDetail().isRetryable());
            assertEquals(30, exception.getProblemDetail().getRetryAfterSeconds());
            assertEquals(connector, exception.getConnector());
            assertFalse(exception.getMessage().contains(secret));
            assertNull(exception.getCause(), "transport internals may contain secret-bearing request data");
            assertTrue(captured.list.stream().noneMatch(event -> event.getFormattedMessage().contains(secret)));
        } finally {
            root.detachAppender(captured);
            captured.stop();
        }
    }

    @Test
    void malformedStatusResponsePropagatesDecodingException() {
        server.stubFor(WireMock.post("/v2/cryptographyProvider/tokens/status")
                .willReturn(WireMock.okJson("{")));

        assertThrows(DecodingException.class,
                () -> client.getTokenStatus(connector, new TokenScopedRequestV2Dto()));
    }
}
