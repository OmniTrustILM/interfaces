package com.otilm.api.clients.v2;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.otilm.api.clients.BaseApiClient;
import com.otilm.api.exception.ConnectionServiceException;
import com.otilm.api.exception.ConnectorClientException;
import com.otilm.api.exception.ConnectorEntityNotFoundException;
import com.otilm.api.exception.ConnectorServerException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.connector.v2.CertificateOperationCancelRequestDto;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.model.core.connector.ConnectorStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CertificateApiClientTest {

    private static final String AUTHORITY_UUID = "6d4c5b3a-2f1e-4d0c-9b8a-7f6e5d4c3b2a";
    private static final String BASE_PATH = "/v2/authorityProvider/authorities/" + AUTHORITY_UUID + "/certificates";
    private static final String ISSUE_CANCEL_PATH = BASE_PATH + "/issue/cancel";
    private static final String REVOKE_CANCEL_PATH = BASE_PATH + "/revoke/cancel";

    private static WireMockServer mockServer;

    private CertificateApiClient client;
    private ConnectorDto connector;

    @BeforeAll
    static void startConnector() {
        mockServer = new WireMockServer(options().dynamicPort());
        mockServer.start();
    }

    @AfterAll
    static void stopConnector() {
        mockServer.stop();
    }

    @BeforeEach
    void createClient() {
        mockServer.resetAll();
        client = new CertificateApiClient(BaseApiClient.prepareWebClient(), null);

        connector = new ConnectorDto();
        connector.setUrl("http://localhost:" + mockServer.port());
        connector.setStatus(ConnectorStatus.CONNECTED);
    }

    @ParameterizedTest
    @ValueSource(strings = {ISSUE_CANCEL_PATH, REVOKE_CANCEL_PATH})
    void returnsNormally_whenCancelAnswers204(String path) {
        // given
        stubCancel(path, WireMock.aResponse().withStatus(204));

        // when
        Executable cancel = cancelAt(path);

        // then
        assertDoesNotThrow(cancel);
    }

    @ParameterizedTest
    @ValueSource(strings = {ISSUE_CANCEL_PATH, REVOKE_CANCEL_PATH})
    void throwsConnectionServiceException_whenCancelAnswers302(String path) {
        // given
        stubCancel(path, WireMock.aResponse().withStatus(302).withHeader("Location", "https://gateway.invalid/login"));

        // when
        Executable cancel = cancelAt(path);

        // then
        ConnectionServiceException thrown = assertThrows(ConnectionServiceException.class, cancel);
        assertEquals(HttpStatus.FOUND, thrown.getHttpStatus());
    }

    @ParameterizedTest
    @ValueSource(strings = {ISSUE_CANCEL_PATH, REVOKE_CANCEL_PATH})
    void throwsConnectionServiceException_whenCancelAnswers307(String path) {
        // given
        stubCancel(path,
                WireMock.aResponse().withStatus(307).withHeader("Location", "https://elsewhere.invalid/cancel"));

        // when
        Executable cancel = cancelAt(path);

        // then
        ConnectionServiceException thrown = assertThrows(ConnectionServiceException.class, cancel);
        assertEquals(HttpStatus.TEMPORARY_REDIRECT, thrown.getHttpStatus());
    }

    @ParameterizedTest
    @ValueSource(strings = {ISSUE_CANCEL_PATH, REVOKE_CANCEL_PATH})
    void throwsValidationException_whenCancelAnswers422(String path) {
        // given
        stubCancel(path,
                WireMock
                        .aResponse()
                        .withStatus(422)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("[\"past the point of no return\"]"));

        // when
        Executable cancel = cancelAt(path);

        // then
        assertThrows(ValidationException.class, cancel);
    }

    @ParameterizedTest
    @ValueSource(strings = {ISSUE_CANCEL_PATH, REVOKE_CANCEL_PATH})
    void throwsEntityNotFound_whenCancelAnswers404(String path) {
        // given
        stubCancel(path, WireMock.aResponse().withStatus(404));

        // when
        Executable cancel = cancelAt(path);

        // then
        assertThrows(ConnectorEntityNotFoundException.class, cancel);
    }

    @ParameterizedTest
    @ValueSource(strings = {ISSUE_CANCEL_PATH, REVOKE_CANCEL_PATH})
    void throwsConnectorClientException_whenCancelAnswers403(String path) {
        // given
        stubCancel(path, WireMock.aResponse().withStatus(403));

        // when
        Executable cancel = cancelAt(path);

        // then
        ConnectorClientException thrown = assertThrows(ConnectorClientException.class, cancel);
        assertEquals(HttpStatus.FORBIDDEN, thrown.getHttpStatus());
    }

    @ParameterizedTest
    @ValueSource(strings = {ISSUE_CANCEL_PATH, REVOKE_CANCEL_PATH})
    void throwsConnectorServerException_whenCancelAnswers500(String path) {
        // given
        stubCancel(path, WireMock.aResponse().withStatus(500));

        // when
        Executable cancel = cancelAt(path);

        // then
        ConnectorServerException thrown = assertThrows(ConnectorServerException.class, cancel);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getHttpStatus());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void stubCancel(String path, ResponseDefinitionBuilder response) {
        mockServer.stubFor(WireMock.post(path).willReturn(response));
    }

    private Executable cancelAt(String path) {
        CertificateOperationCancelRequestDto request = new CertificateOperationCancelRequestDto();
        if (ISSUE_CANCEL_PATH.equals(path)) {
            return () -> client.cancelIssueCertificate(connector, AUTHORITY_UUID, request);
        }
        return () -> client.cancelRevokeCertificate(connector, AUTHORITY_UUID, request);
    }
}
