package com.otilm.api.clients.v2;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.otilm.api.clients.BaseApiClient;
import com.otilm.api.clients.cryptography.v2.TokenApiClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.ConnectorServerException;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.v2.OperationResponseValidator;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenStatusResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenStatusV2;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.model.core.connector.ConnectorStatus;
import com.otilm.api.model.core.cryptography.key.KeyUsage;
import com.otilm.api.testsupport.ValidatorFixture;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.withValidTokenProfileScope;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.withValidTokenScope;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.argumentSet;

class TokenApiClientTest {

    private static final String BASE_PATH = "/v2/cryptographyProvider/tokens";
    private static final String ATTRIBUTES_PATH = BASE_PATH + "/attributes";
    private static final String STATUS_PATH = BASE_PATH + "/status";
    private static final String PROFILE_ATTRIBUTES_PATH = BASE_PATH + "/tokenProfile/attributes";
    private static final String PROFILE_KEY_USAGES_PATH = BASE_PATH + "/tokenProfile/keyUsages";
    private static final String KEY_REQUEST_TYPES_PATH = BASE_PATH + "/keyRequestTypes";
    private static final String ATTRIBUTE_NAME = "tokenAttribute";
    private static final String TOKEN_STATUS_DETAIL = "Token is available";
    private static final String VALID_ATTRIBUTE_LIST_JSON = """
            [
              {
                "uuid": "11111111-1111-1111-1111-111111111111",
                "name": "tokenAttribute",
                "type": "data",
                "contentType": "string",
                "version": 2
              }
            ]
            """;

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();

    private TokenApiClient client;
    private ConnectorDto connector;
    private static WireMockServer mockServer;

    @BeforeAll
    static void startMockServer() {
        mockServer = new WireMockServer(options().dynamicPort());
        mockServer.start();
        WireMock.configureFor("localhost", mockServer.port());
    }

    @AfterAll
    static void stopMockServer() {
        mockServer.stop();
    }

    @BeforeEach
    void setUp() {
        mockServer.resetAll();

        connector = new ConnectorDto();
        connector.setName("cryptography connector");
        connector.setUrl("http://localhost:" + mockServer.port());
        connector.setStatus(ConnectorStatus.CONNECTED);

        OperationResponseValidator responseValidator = new OperationResponseValidator(VALIDATORS.validator());
        client = new TokenApiClient(BaseApiClient.prepareWebClient(), null, responseValidator);
    }

    @Test
    void listTokenAttributes_getsRequestAndReturnsAttributes() throws ConnectorException {
        // given
        stubGetJsonResponse(ATTRIBUTES_PATH, HttpStatus.OK, VALID_ATTRIBUTE_LIST_JSON);

        // when
        List<BaseAttribute> result = client.listTokenAttributes(connector);

        // then
        assertEquals(1, result.size());
        assertEquals(ATTRIBUTE_NAME, result.get(0).getName());
        mockServer.verify(WireMock.getRequestedFor(WireMock.urlEqualTo(ATTRIBUTES_PATH)));
    }

    @Test
    void getTokenStatus_postsRequestAndReturnsStatus() throws ConnectorException {
        // given
        String responseJson = """
                {"status":"Connected","detail":"Token is available"}
                """;
        stubJsonResponse(STATUS_PATH, HttpStatus.OK, responseJson);

        // when
        TokenStatusResponseV2Dto result = client.getTokenStatus(connector, tokenScopedRequest());

        // then
        assertEquals(TokenStatusV2.CONNECTED, result.getStatus());
        assertEquals(TOKEN_STATUS_DETAIL, result.getDetail());
        verifyTokenScopedRequest(STATUS_PATH);
    }

    @Test
    void getTokenStatus_rejectsInvalidResponse() {
        // given
        String responseWithoutStatus = "{}";
        stubJsonResponse(STATUS_PATH, HttpStatus.OK, responseWithoutStatus);

        // when
        Executable call = () -> client.getTokenStatus(connector, tokenScopedRequest());

        // then
        ConnectorException exception = assertThrows(ConnectorException.class, call);
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        assertSame(connector, exception.getConnector());
    }

    @Test
    void listTokenProfileAttributes_postsRequestAndReturnsAttributes() throws ConnectorException {
        // given
        stubJsonResponse(PROFILE_ATTRIBUTES_PATH, HttpStatus.OK, VALID_ATTRIBUTE_LIST_JSON);

        // when
        List<BaseAttribute> result = client.listTokenProfileAttributes(connector, tokenScopedRequest());

        // then
        assertEquals(1, result.size());
        assertEquals(ATTRIBUTE_NAME, result.get(0).getName());
        verifyTokenScopedRequest(PROFILE_ATTRIBUTES_PATH);
    }

    @Test
    void listTokenProfileKeyUsages_postsRequestAndReturnsKeyUsages() throws ConnectorException {
        // given
        String responseJson = """
                ["sign","encrypt"]
                """;
        stubJsonResponse(PROFILE_KEY_USAGES_PATH, HttpStatus.OK, responseJson);

        // when
        List<KeyUsage> result = client.listTokenProfileKeyUsages(connector, tokenScopedRequest());

        // then
        assertEquals(List.of(KeyUsage.SIGN, KeyUsage.ENCRYPT), result);
        verifyTokenScopedRequest(PROFILE_KEY_USAGES_PATH);
    }

    @Test
    void listSupportedKeyRequestTypes_postsRequestAndReturnsKeyRequestTypes() throws ConnectorException {
        // given
        String responseJson = """
                ["secret","keyPair"]
                """;
        stubJsonResponse(KEY_REQUEST_TYPES_PATH, HttpStatus.OK, responseJson);

        // when
        List<KeyRequestType> result = client.listSupportedKeyRequestTypes(connector, tokenProfileScopedRequest());

        // then
        assertEquals(List.of(KeyRequestType.SECRET, KeyRequestType.KEY_PAIR), result);
        mockServer
                .verify(WireMock
                        .postRequestedFor(WireMock.urlEqualTo(KEY_REQUEST_TYPES_PATH))
                        .withRequestBody(WireMock.equalToJson("""
                                {
                                  "tokenAttributes": [],
                                  "tokenProfileAttributes": []
                                }
                                """)));
    }

    @ParameterizedTest
    @MethodSource("missingKeyRequestTypeBodies")
    void listSupportedKeyRequestTypes_rejectsMissingBody(HttpStatus status, String responseBody) {
        // given
        stubJsonResponse(KEY_REQUEST_TYPES_PATH, status, responseBody);
        String expectedError = "Connector response body is required";

        // when
        Executable call = () -> client.listSupportedKeyRequestTypes(connector, tokenProfileScopedRequest());

        // then
        assertConnectorValidationFailure(call, expectedError);
    }

    static Stream<Arguments> missingKeyRequestTypeBodies() {
        return Stream
                .of(argumentSet("HTTP 200 without a body", HttpStatus.OK, ""),
                        argumentSet("HTTP 200 with JSON null", HttpStatus.OK, "null"),
                        argumentSet("HTTP 204 without a body", HttpStatus.NO_CONTENT, ""));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("keyRequestTypeBodiesWithNullElements")
    void listSupportedKeyRequestTypes_rejectsNullElements(String responseBody) {
        // given
        stubJsonResponse(KEY_REQUEST_TYPES_PATH, HttpStatus.OK, responseBody);
        String expectedError = "Connector response must not contain a null key request type";

        // when
        Executable call = () -> client.listSupportedKeyRequestTypes(connector, tokenProfileScopedRequest());

        // then
        assertConnectorValidationFailure(call, expectedError);
    }

    static Stream<Named<String>> keyRequestTypeBodiesWithNullElements() {
        return Stream
                .of(named("null element only", "[null]"), named("valid type followed by null", "[\"secret\",null]"),
                        named("null followed by valid type", "[null,\"keyPair\"]"));
    }

    @Test
    void listSupportedKeyRequestTypes_acceptsEmptyList() throws ConnectorException {
        // given
        String emptyListResponse = "[]";
        stubJsonResponse(KEY_REQUEST_TYPES_PATH, HttpStatus.OK, emptyListResponse);

        // when
        List<KeyRequestType> result = client.listSupportedKeyRequestTypes(connector, tokenProfileScopedRequest());

        // then
        assertEquals(List.of(), result);
    }

    @Test
    void getTokenStatus_propagatesConnectorHttpError() {
        // given
        HttpStatus connectorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String connectorError = "connector failure";
        mockServer
                .stubFor(WireMock
                        .post(STATUS_PATH)
                        .willReturn(WireMock.aResponse().withStatus(connectorStatus.value()).withBody(connectorError)));

        // when
        Executable call = () -> client.getTokenStatus(connector, tokenScopedRequest());

        // then
        ConnectorServerException exception = assertThrows(ConnectorServerException.class, call);
        assertEquals(connectorStatus, exception.getHttpStatus());
        assertSame(connector, exception.getConnector());
    }

    private void verifyTokenScopedRequest(String path) {
        mockServer.verify(WireMock.postRequestedFor(WireMock.urlEqualTo(path)).withRequestBody(WireMock.equalToJson("""
                {"tokenAttributes": []}
                """)));
    }

    private void assertConnectorValidationFailure(Executable call, String expectedMessage) {
        ConnectorException exception = assertThrows(ConnectorException.class, call);
        assertEquals(expectedMessage, exception.getMessage());
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        assertSame(connector, exception.getConnector());
    }

    private void stubJsonResponse(String path, HttpStatus status, String body) {
        stubJsonResponse(WireMock.post(path), status, body);
    }

    private void stubGetJsonResponse(String path, HttpStatus status, String body) {
        stubJsonResponse(WireMock.get(path), status, body);
    }

    private void stubJsonResponse(MappingBuilder request, HttpStatus status, String body) {
        mockServer
                .stubFor(request
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(status.value())
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(body)));
    }

    private static TokenScopedRequestV2Dto tokenScopedRequest() {
        return withValidTokenScope(new TokenScopedRequestV2Dto());
    }

    private static TokenProfileScopedRequestV2Dto tokenProfileScopedRequest() {
        return withValidTokenProfileScope(new TokenProfileScopedRequestV2Dto());
    }
}
