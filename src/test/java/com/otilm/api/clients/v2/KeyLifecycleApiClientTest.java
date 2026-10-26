package com.otilm.api.clients.v2;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.otilm.api.clients.BaseApiClient;
import com.otilm.api.clients.cryptography.v2.KeyLifecycleApiClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.ConnectorServerException;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.OperationResponseValidator;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CertificateIssuedResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CertificateRequestCreationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.OperationEventType;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.model.core.connector.ConnectorStatus;
import com.otilm.api.testsupport.ValidatorFixture;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadata;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.withValidTokenProfileScope;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.BOUND_SIGNER;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.CERTIFICATE_REQUEST_ALGORITHM;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.CERTIFICATE_UUID;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.certificateFiller;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.certificationRequestInfo;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.pkcs10;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.validBoundCertificateRequest;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.validCertificateIssuedRequest;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks HTTP request binding and connector response contracts for the key lifecycle hooks.
 */
class KeyLifecycleApiClientTest {

    private static final String BASE_PATH = "/v2/cryptographyProvider/keyLifecycle";
    private static final String SIGNER_ATTRIBUTES_PATH = BASE_PATH + "/signer/attributes";
    private static final String CERTIFICATE_REQUEST_PATH = BASE_PATH + "/csr";
    private static final String CERTIFICATE_REQUEST_ATTRIBUTES_PATH = CERTIFICATE_REQUEST_PATH + "/attributes";
    private static final String CERTIFICATE_ISSUED_PATH = BASE_PATH + "/certificate";
    private static final String METADATA_NAME = "provider handle";
    private static final String SIGNER_ATTRIBUTE_LIST_JSON = """
            [
              {
                "uuid": "2b7e1d3c-9a4f-4e6b-b1c8-0d5a7f9e3c21",
                "name": "id",
                "type": "data",
                "contentType": "string",
                "version": 3,
                "properties": {"label": "Signer id", "required": true}
              }
            ]
            """;
    private static final String V2_SIGNER_ATTRIBUTE_LIST_JSON = """
            [
              {
                "uuid": "2b7e1d3c-9a4f-4e6b-b1c8-0d5a7f9e3c21",
                "name": "id",
                "type": "data",
                "contentType": "string",
                "properties": {"label": "Signer id", "required": true}
              }
            ]
            """;
    private static final String CERTIFICATE_REQUEST_ATTRIBUTE_LIST_JSON = """
            [
              {
                "uuid": "9180267f-c82f-4b7b-8160-d2363d813869",
                "name": "signatureAlgorithm",
                "type": "data",
                "contentType": "string",
                "version": 3,
                "properties": {"label": "Signature Algorithm", "required": true, "list": true},
                "content": [{"contentType": "string", "data": "SHA256withRSA"}]
              }
            ]
            """;
    private static final String EVENTS_JSON = """
            {"events": [{"type": "operationAccepted", "at": "2026-10-25T10:00:00Z"}]}
            """;
    private static final String EVENT_WITHOUT_TYPE_JSON = """
            {"events": [{"at": "2026-10-25T10:00:00Z"}]}
            """;

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();

    // The connector WebClient pools connections per host and port for 30 seconds.
    // One server per class keeps every pooled connection on a live port.
    private static WireMockServer mockServer;

    private KeyLifecycleApiClient client;
    private ConnectorDto connector;

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
        connector.setName("key lifecycle connector");
        connector.setUrl("http://localhost:" + mockServer.port());
        connector.setStatus(ConnectorStatus.CONNECTED);

        OperationResponseValidator responseValidator = new OperationResponseValidator(VALIDATORS.validator());
        client = new KeyLifecycleApiClient(BaseApiClient.prepareWebClient(), null, responseValidator);
    }

    @Test
    void listSignerAttributes_postsTheTokenContextAndReturnsTheDefinitions() throws ConnectorException {
        // given
        stubJsonResponse(SIGNER_ATTRIBUTES_PATH, HttpStatus.OK, SIGNER_ATTRIBUTE_LIST_JSON);

        // when
        List<BaseAttribute> result = client.listSignerAttributes(connector, tokenProfileScopedRequest());

        // then
        assertEquals(1, result.size());
        assertEquals("id", result.get(0).getName());
        RequestPatternBuilder request = WireMock
                .postRequestedFor(WireMock.urlEqualTo(SIGNER_ATTRIBUTES_PATH))
                .withRequestBody(WireMock.equalToJson("{\"tokenAttributes\": [], \"tokenProfileAttributes\": []}"));
        mockServer.verify(request);
    }

    @Test
    void listSignerAttributes_rejectsAV2Attribute() {
        // given
        stubJsonResponse(SIGNER_ATTRIBUTES_PATH, HttpStatus.OK, V2_SIGNER_ATTRIBUTE_LIST_JSON);

        // when
        Executable call = () -> client.listSignerAttributes(connector, tokenProfileScopedRequest());

        // then
        ConnectorException exception = assertThrows(ConnectorException.class, call);
        assertEquals("Signer attributes must be v3 data attributes", exception.getMessage());
        assertSame(connector, exception.getConnector());
    }

    @Test
    void listCertificateRequestAttributes_postsTheKeyAndReturnsTheAlgorithmListing() throws ConnectorException {
        // given
        stubJsonResponse(CERTIFICATE_REQUEST_ATTRIBUTES_PATH, HttpStatus.OK, CERTIFICATE_REQUEST_ATTRIBUTE_LIST_JSON);

        // when
        List<BaseAttribute> result = client.listCertificateRequestAttributes(connector, keyScopedRequest());

        // then
        assertEquals(1, result.size());
        assertEquals("signatureAlgorithm", result.get(0).getName());
        RequestPatternBuilder request = WireMock
                .postRequestedFor(WireMock.urlEqualTo(CERTIFICATE_REQUEST_ATTRIBUTES_PATH))
                .withRequestBody(WireMock.matchingJsonPath("$.keyMeta[0].name", WireMock.equalTo(METADATA_NAME)));
        mockServer.verify(request);
    }

    @Test
    void listCertificateRequestAttributes_rejectsAListingWithoutTheSignatureAlgorithm() {
        // given
        stubJsonResponse(CERTIFICATE_REQUEST_ATTRIBUTES_PATH, HttpStatus.OK, SIGNER_ATTRIBUTE_LIST_JSON);

        // when
        Executable call = () -> client.listCertificateRequestAttributes(connector, keyScopedRequest());

        // then
        ConnectorException exception = assertThrows(ConnectorException.class, call);
        assertTrue(exception.getMessage().startsWith("Certificate request attributes"), exception.getMessage());
    }

    @Test
    void createCertificateRequest_postsTheRequestInfoAndReturnsThePkcs10() throws ConnectorException {
        // given
        byte[] expectedPkcs10 = pkcs10(certificationRequestInfo(), CERTIFICATE_REQUEST_ALGORITHM);
        stubJsonResponse(CERTIFICATE_REQUEST_PATH, HttpStatus.OK, certificateRequestResponseJson(expectedPkcs10));
        String requestInfo = Base64.getEncoder().encodeToString(certificationRequestInfo());

        // when
        CertificateRequestCreationResponseV2Dto result = client
                .createCertificateRequest(connector, validBoundCertificateRequest());

        // then
        assertArrayEquals(expectedPkcs10, result.getPkcs10());
        assertEquals(BOUND_SIGNER, result.getSigner());
        RequestPatternBuilder request = WireMock
                .postRequestedFor(WireMock.urlEqualTo(CERTIFICATE_REQUEST_PATH))
                .withRequestBody(WireMock.matchingJsonPath("$.certificationRequestInfo", WireMock.equalTo(requestInfo)))
                .withRequestBody(WireMock
                        .matchingJsonPath("$.signatureAlgorithm",
                                WireMock.equalTo(CERTIFICATE_REQUEST_ALGORITHM.getCode())))
                .withRequestBody(WireMock.matchingJsonPath("$.signer", WireMock.equalTo(BOUND_SIGNER)));
        mockServer.verify(request);
    }

    @Test
    void createCertificateRequest_rejectsAPkcs10ForAnotherSubject() {
        // given
        byte[] otherPkcs10 = pkcs10(certificationRequestInfo("CN=John Roe"), CERTIFICATE_REQUEST_ALGORITHM);
        stubJsonResponse(CERTIFICATE_REQUEST_PATH, HttpStatus.OK, certificateRequestResponseJson(otherPkcs10));

        // when
        Executable call = () -> client.createCertificateRequest(connector, validBoundCertificateRequest());

        // then
        ConnectorException exception = assertThrows(ConnectorException.class, call);
        assertTrue(exception.getMessage().contains("another subject"), exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(value = HttpStatus.class, names = {"CREATED", "ACCEPTED", "PARTIAL_CONTENT"})
    void createCertificateRequest_rejectsUnexpectedSuccessStatusWithValidBody(HttpStatus status) {
        // given
        byte[] validPkcs10 = pkcs10(certificationRequestInfo(), CERTIFICATE_REQUEST_ALGORITHM);
        stubJsonResponse(CERTIFICATE_REQUEST_PATH, status, certificateRequestResponseJson(validPkcs10));

        // when
        Executable call = () -> client.createCertificateRequest(connector, validBoundCertificateRequest());

        // then
        assertUnexpectedSuccessStatus(status, call);
    }

    @Test
    void certificateIssued_postsTheCertificateItsChainAndIdentifierAndReturnsTheEvents() throws ConnectorException {
        // given
        stubJsonResponse(CERTIFICATE_ISSUED_PATH, HttpStatus.OK, EVENTS_JSON);
        String filler = Base64.getEncoder().encodeToString(certificateFiller());

        // when
        CertificateIssuedResponseV2Dto result = client.certificateIssued(connector, validCertificateIssuedRequest());

        // then
        assertEquals(1, result.getEvents().size());
        assertEquals(OperationEventType.OPERATION_ACCEPTED, result.getEvents().get(0).getType());
        RequestPatternBuilder request = WireMock
                .postRequestedFor(WireMock.urlEqualTo(CERTIFICATE_ISSUED_PATH))
                .withRequestBody(WireMock.matchingJsonPath("$.keyMeta[0].name", WireMock.equalTo(METADATA_NAME)))
                .withRequestBody(WireMock.matchingJsonPath("$.certificate", WireMock.equalTo(filler)))
                .withRequestBody(WireMock.matchingJsonPath("$.chain[0]", WireMock.equalTo(filler)))
                .withRequestBody(
                        WireMock.matchingJsonPath("$.certificateUuid", WireMock.equalTo(CERTIFICATE_UUID.toString())));
        mockServer.verify(request);
    }

    @Test
    void certificateIssued_rejectsAnEventWithoutItsType() {
        // given
        stubJsonResponse(CERTIFICATE_ISSUED_PATH, HttpStatus.OK, EVENT_WITHOUT_TYPE_JSON);

        // when
        Executable call = () -> client.certificateIssued(connector, validCertificateIssuedRequest());

        // then
        assertThrows(ConnectorException.class, call);
    }

    @ParameterizedTest
    @EnumSource(value = HttpStatus.class, names = {"CREATED", "ACCEPTED", "PARTIAL_CONTENT"})
    void certificateIssued_rejectsUnexpectedSuccessStatusWithValidBody(HttpStatus status) {
        // given
        stubJsonResponse(CERTIFICATE_ISSUED_PATH, status, EVENTS_JSON);

        // when
        Executable call = () -> client.certificateIssued(connector, validCertificateIssuedRequest());

        // then
        assertUnexpectedSuccessStatus(status, call);
    }

    @Test
    void certificateIssued_propagatesConnectorHttpError() {
        // given
        HttpStatus connectorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        mockServer
                .stubFor(WireMock
                        .post(CERTIFICATE_ISSUED_PATH)
                        .willReturn(WireMock.aResponse().withStatus(connectorStatus.value()).withBody("failure")));

        // when
        Executable call = () -> client.certificateIssued(connector, validCertificateIssuedRequest());

        // then
        ConnectorServerException exception = assertThrows(ConnectorServerException.class, call);
        assertEquals(connectorStatus, exception.getHttpStatus());
        assertSame(connector, exception.getConnector());
    }

    private void assertUnexpectedSuccessStatus(HttpStatus status, Executable call) {
        ConnectorException exception = assertThrows(ConnectorException.class, call);
        assertEquals("Connector returned HTTP " + status.value() + "; expected HTTP 200", exception.getMessage());
        assertSame(connector, exception.getConnector());
    }

    private static void stubJsonResponse(String path, HttpStatus status, String body) {
        mockServer
                .stubFor(WireMock
                        .post(path)
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(status.value())
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(body)));
    }

    private static String certificateRequestResponseJson(byte[] pkcs10) {
        return """
                {"pkcs10": "%s", "signer": "%s"}
                """.formatted(Base64.getEncoder().encodeToString(pkcs10), BOUND_SIGNER);
    }

    private static TokenProfileScopedRequestV2Dto tokenProfileScopedRequest() {
        return withValidTokenProfileScope(new TokenProfileScopedRequestV2Dto());
    }

    private static KeyScopedRequestV2Dto keyScopedRequest() {
        KeyScopedRequestV2Dto request = withValidTokenProfileScope(new KeyScopedRequestV2Dto());
        request.setKeyMeta(validMetadata());
        return request;
    }
}
