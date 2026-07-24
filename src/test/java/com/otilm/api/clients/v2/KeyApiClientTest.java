package com.otilm.api.clients.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.otilm.api.clients.BaseApiClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.ConnectorProblemException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.error.ErrorCode;
import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.CreateKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.PrivateKeyDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.PublicKeyDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.SecretKeyDataV2Dto;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.model.core.connector.ConnectorStatus;
import com.otilm.api.model.core.cryptography.key.KeyUsage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.MediaType;

import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.otilm.api.model.connector.cryptography.v2.MetadataTestUtils.stringMetadata;
import static org.junit.jupiter.api.Assertions.*;

class KeyApiClientTest {
    private KeyApiClient client;
    private ConnectorDto connector;
    private WireMockServer server;

    @BeforeEach
    void setUp() {
        client = new KeyApiClient(BaseApiClient.prepareWebClient(), null);
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
    void invokesEverySynchronousKeyCreationRoute() throws Exception {
        server.stubFor(WireMock.post("/v2/cryptographyProvider/keys/types")
                .willReturn(WireMock.okJson("[\"secret\",\"keyPair\"]")));
        server.stubFor(WireMock.post("/v2/cryptographyProvider/keys/secret/attributes")
                .willReturn(WireMock.okJson("[]")));
        server.stubFor(WireMock.post("/v2/cryptographyProvider/keys/pair/attributes")
                .willReturn(WireMock.okJson("[]")));
        server.stubFor(WireMock.post("/v2/cryptographyProvider/keys/secret")
                .willReturn(WireMock.okJson("{\"keyMeta\":[" + metadataJson("keyId", "secret-1") + "],\"keyData\":{"
                        + "\"type\":\"Secret\",\"algorithm\":\"AES\",\"length\":256}}")));
        server.stubFor(WireMock.post("/v2/cryptographyProvider/keys/pair")
                .willReturn(WireMock.okJson("{\"keyPairMeta\":[" + metadataJson("pairId", "pair-1") + "],"
                        + "\"privateKeyData\":{\"keyMeta\":[" + metadataJson("keyId", "private-1") + "],\"keyData\":{"
                        + "\"type\":\"Private\",\"algorithm\":\"RSA\",\"length\":3072}},"
                        + "\"publicKeyData\":{\"keyMeta\":[" + metadataJson("keyId", "public-1") + "],\"keyData\":{"
                        + "\"type\":\"Public\",\"algorithm\":\"RSA\",\"length\":3072}}}")));

        TokenProfileScopedRequestV2Dto context = new TokenProfileScopedRequestV2Dto();
        context.setTokenAttributes(List.of());
        context.setTokenProfileAttributes(List.of());
        context.setKeyUsages(Set.of(KeyUsage.SIGN));
        CreateKeyRequestV2Dto create = synchronousCreateRequest();
        create.setKeyCreationId("6c87b73a-5659-4d17-b625-abc72cd94150");
        create.setTokenAttributes(List.of());
        create.setTokenProfileAttributes(List.of());
        create.setCreateKeyAttributes(List.of());

        assertEquals(List.of(KeyRequestType.SECRET, KeyRequestType.KEY_PAIR),
                client.listSupportedKeyTypes(connector, context));
        assertTrue(client.listCreateKeyAttributes(connector, KeyRequestType.SECRET, context).isEmpty());
        assertTrue(client.listCreateKeyAttributes(connector, KeyRequestType.KEY_PAIR, context).isEmpty());
        var secretResponse = client.createSecretKey(connector, create).getBody();
        assertNotNull(secretResponse);
        assertInstanceOf(SecretKeyDataV2Dto.class, secretResponse.getKeyData());
        var pairResponse = client.createKeyPair(connector, create).getBody();
        assertNotNull(pairResponse);
        assertInstanceOf(PrivateKeyDataV2Dto.class,
                pairResponse.getPrivateKeyData().getKeyData());
        assertInstanceOf(PublicKeyDataV2Dto.class,
                pairResponse.getPublicKeyData().getKeyData());
        server.verify(1, WireMock.postRequestedFor(WireMock.urlPathEqualTo(
                        "/v2/cryptographyProvider/keys/pair"))
                .withRequestBody(WireMock.matchingJsonPath("$.keyCreationId",
                        WireMock.equalTo("6c87b73a-5659-4d17-b625-abc72cd94150"))));
    }

    @Test
    void rejectsNullSupportedTypeEntries() {
        server.stubFor(WireMock.post("/v2/cryptographyProvider/keys/types")
                .willReturn(WireMock.okJson("[\"secret\",null]")));

        assertThrows(ConnectorException.class,
                () -> client.listSupportedKeyTypes(connector, new TokenProfileScopedRequestV2Dto()));
    }

    @Test
    void preservesRequestedAsynchronousCreation() throws Exception {
        server.stubFor(WireMock.post("/v2/cryptographyProvider/keys/secret")
                .willReturn(WireMock.aResponse().withStatus(202)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"operationMeta\":[" + metadataJson("operationId", "123") + "]}")));

        CreateKeyRequestV2Dto request = new CreateKeyRequestV2Dto();
        request.setExecutionMode(OperationExecutionMode.ASYNCHRONOUS);
        var response = client.createSecretKey(connector, request);
        assertEquals(202, response.getStatusCode().value());
        assertEquals("123", assertResponseBody(response).getOperationMeta().get(0)
                .getContent().get(0).getData());
    }

    @Test
    void createSecretKeyPreservesStructuredConnectorProblem() {
        String problemJson = """
                {
                  "type": "https://docs.otilm.com/problems/common/RATE_LIMIT_EXCEEDED",
                  "title": "Rate limit exceeded",
                  "status": 429,
                  "detail": "Provider request quota exhausted",
                  "errorCode": "RATE_LIMIT_EXCEEDED",
                  "timestamp": "2026-07-23T10:00:00Z",
                  "correlationId": "key-test-correlation",
                  "retryable": true,
                  "retryAfterSeconds": 15
                }
                """;
        server.stubFor(WireMock.post("/v2/cryptographyProvider/keys/secret")
                .willReturn(WireMock.aResponse()
                        .withStatus(429)
                        .withHeader("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                        .withBody(problemJson)));

        ConnectorProblemException error = assertThrows(
                ConnectorProblemException.class,
                () -> client.createSecretKey(connector, synchronousCreateRequest()));

        assertEquals(ErrorCode.RATE_LIMIT_EXCEEDED, error.getProblemDetail().getErrorCode());
        assertEquals(429, error.getProblemDetail().getStatus());
        assertTrue(error.getProblemDetail().isRetryable());
        assertEquals(15, error.getProblemDetail().getRetryAfterSeconds());
        assertEquals("key-test-correlation", error.getProblemDetail().getCorrelationId());
        assertEquals(connector, error.getConnector());
    }

    @Test
    void createSecretKeyDoesNotFlattenLocalConnectorStatusValidation() {
        connector.setStatus(ConnectorStatus.WAITING_FOR_APPROVAL);

        assertThrows(ValidationException.class,
                () -> client.createSecretKey(connector, synchronousCreateRequest()));
    }

    @Test
    void rejectsPublicKeyOnSecretEndpoint() {
        server.stubFor(WireMock.post("/v2/cryptographyProvider/keys/secret")
                .willReturn(WireMock.okJson("{\"keyMeta\":[],\"keyData\":{"
                        + "\"type\":\"Public\",\"algorithm\":\"RSA\",\"length\":2048}}")));

        assertThrows(DecodingException.class,
                () -> client.createSecretKey(connector, synchronousCreateRequest()));
    }

    @Test
    void rejectsLegacySecretKeyMaterial() {
        server.stubFor(WireMock.post("/v2/cryptographyProvider/keys/secret")
                .willReturn(WireMock.okJson("{\"keyMeta\":[],\"keyData\":{"
                        + "\"type\":\"Secret\",\"algorithm\":\"AES\",\"length\":256,"
                        + "\"format\":\"Raw\",\"value\":{\"value\":\"c2VjcmV0\"}}}")));

        assertThrows(DecodingException.class,
                () -> client.createSecretKey(connector, synchronousCreateRequest()));
    }

    @Test
    void rejectsPrivateAndEncryptedPrivateKeyMaterialOnPairEndpoint() {
        for (String format : List.of("PrivateKeyInfo", "EncryptedPrivateKeyInfo")) {
            server.resetAll();
            server.stubFor(WireMock.post("/v2/cryptographyProvider/keys/pair")
                    .willReturn(WireMock.okJson("{\"keyPairMeta\":[],"
                            + "\"privateKeyData\":{\"keyMeta\":[],\"keyData\":{"
                            + "\"type\":\"Private\",\"algorithm\":\"RSA\",\"length\":2048,"
                            + "\"format\":\"" + format + "\",\"value\":{\"value\":\"cHJpdmF0ZQ==\"}}},"
                            + "\"publicKeyData\":{\"keyMeta\":[],\"keyData\":{"
                            + "\"type\":\"Public\",\"algorithm\":\"RSA\",\"length\":2048}}}")));

            assertThrows(DecodingException.class,
                    () -> client.createKeyPair(connector, synchronousCreateRequest()));
        }
    }

    @Test
    void rejectsKeyMaterialPlacedOnOuterEnvelope() {
        server.stubFor(WireMock.post("/v2/cryptographyProvider/keys/secret")
                .willReturn(WireMock.okJson("{\"keyMeta\":[],\"value\":\"c2VjcmV0\",\"keyData\":{"
                        + "\"type\":\"Secret\",\"algorithm\":\"AES\",\"length\":256}}")));

        assertThrows(DecodingException.class,
                () -> client.createSecretKey(connector, synchronousCreateRequest()));
    }

    @Test
    void rejectsMalformedConnectorMetadataShape() {
        server.stubFor(WireMock.post("/v2/cryptographyProvider/keys/secret")
                .willReturn(WireMock.okJson("{\"keyMeta\":[{\"name\":\"leak\","
                        + "\"secret\":\"c2VjcmV0LWtleQ==\"}],\"keyData\":{"
                        + "\"type\":\"Secret\",\"algorithm\":\"AES\",\"length\":256}}")));

        assertThrows(ConnectorException.class,
                () -> client.createSecretKey(connector, synchronousCreateRequest()));
    }

    @Test
    void rejectsMalformedCompletedDescriptor() {
        server.stubFor(WireMock.post("/v2/cryptographyProvider/keys/secret")
                .willReturn(WireMock.okJson("{\"keyMeta\":[],\"keyData\":{"
                        + "\"type\":\"Secret\",\"length\":256}}")));

        ConnectorException error = assertThrows(ConnectorException.class,
                () -> client.createSecretKey(connector, synchronousCreateRequest()));
        assertTrue(error.getMessage().contains("invalid completed key data"));
    }

    @Test
    void rejectsPublicSpkiThatDoesNotMatchDeclaredAlgorithm() throws Exception {
        String rsaSpki = Base64.getEncoder().encodeToString(
                KeyPairGenerator.getInstance("RSA").generateKeyPair().getPublic().getEncoded());
        server.stubFor(WireMock.post("/v2/cryptographyProvider/keys/pair")
                .willReturn(WireMock.okJson("{\"keyPairMeta\":[],"
                        + "\"privateKeyData\":{\"keyMeta\":[],\"keyData\":{"
                        + "\"type\":\"Private\",\"algorithm\":\"ECDSA\",\"length\":256}},"
                        + "\"publicKeyData\":{\"keyMeta\":[],\"keyData\":{"
                        + "\"type\":\"Public\",\"algorithm\":\"ECDSA\",\"length\":256,"
                        + "\"publicKeySpki\":\"" + rsaSpki + "\"}}}")));

        ConnectorException error = assertThrows(ConnectorException.class,
                () -> client.createKeyPair(connector, synchronousCreateRequest()));
        assertTrue(error.getMessage().contains("invalid completed key data"));
    }

    @Test
    void rejectsSwappedKeyPairRoles() {
        server.stubFor(WireMock.post("/v2/cryptographyProvider/keys/pair")
                .willReturn(WireMock.okJson("{\"keyPairMeta\":[],"
                        + "\"privateKeyData\":{\"keyMeta\":[],\"keyData\":{"
                        + "\"type\":\"Public\",\"algorithm\":\"RSA\",\"length\":2048}},"
                        + "\"publicKeyData\":{\"keyMeta\":[],\"keyData\":{"
                        + "\"type\":\"Private\",\"algorithm\":\"RSA\",\"length\":2048}}}")));

        assertThrows(DecodingException.class,
                () -> client.createKeyPair(connector, synchronousCreateRequest()));
    }

    private static CreateKeyRequestV2Dto synchronousCreateRequest() {
        CreateKeyRequestV2Dto request = new CreateKeyRequestV2Dto();
        request.setExecutionMode(OperationExecutionMode.SYNCHRONOUS);
        return request;
    }

    private static <T> T assertResponseBody(org.springframework.http.ResponseEntity<T> response) {
        assertNotNull(response.getBody());
        return response.getBody();
    }

    private static String metadataJson(String name, String value) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(stringMetadata(name, value));
    }
}
