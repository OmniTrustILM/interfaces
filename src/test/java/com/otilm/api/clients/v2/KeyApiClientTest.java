package com.otilm.api.clients.v2;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.otilm.api.clients.BaseApiClient;
import com.otilm.api.clients.cryptography.v2.KeyApiClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.ConnectorServerException;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.common.v2.OperationStatus;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.OperationResponseValidator;
import com.otilm.api.model.connector.cryptography.v2.OperationTrackingRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.CreateKeyAttributesRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.CreateKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.DestroyKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ExportKeyResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ExportableKeyTypeV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportKeyAttributesRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportKeyResultRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportableKeyTypeV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyCreationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyCreationStatusResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyDestructionStatusResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyOperationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyOperationStatusResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.SecretKeyDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.SecretKeyOperationStatusResponseV2Dto;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.model.core.connector.ConnectorStatus;
import com.otilm.api.testsupport.ValidatorFixture;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.EXPORT_PASSPHRASE;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.KEY_REFERENCE;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.pinnedProtectionEnvelope;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validEncryptedKeyMaterial;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validExportKeyRequest;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadata;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validPublicKeyData;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.withValidTokenProfileScope;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.withValidTokenScope;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KeyApiClientTest {

    private static final String BASE_PATH = "/v2/cryptographyProvider/keys";
    private static final String CREATE_ATTRIBUTES_PATH = BASE_PATH + "/create/attributes";
    private static final String CREATE_STATUS_PATH = BASE_PATH + "/create/status";
    private static final String CREATE_CANCEL_PATH = BASE_PATH + "/create/cancel";
    private static final String DESTROY_PATH = BASE_PATH + "/destroy";
    private static final String DESTROY_STATUS_PATH = BASE_PATH + "/destroy/status";
    private static final String DESTROY_CANCEL_PATH = BASE_PATH + "/destroy/cancel";
    private static final String EXPORT_PATH = BASE_PATH + "/export";
    private static final String EXPORT_KEY_TYPES_PATH = EXPORT_PATH + "/keyTypes";
    private static final String EXPORT_ATTRIBUTES_PATH = EXPORT_PATH + "/attributes";
    private static final String IMPORT_PATH = BASE_PATH + "/import";
    private static final String IMPORT_KEY_TYPES_PATH = IMPORT_PATH + "/keyTypes";
    private static final String IMPORT_ATTRIBUTES_PATH = IMPORT_PATH + "/attributes";
    private static final String IMPORT_STATUS_PATH = IMPORT_PATH + "/status";
    private static final String IMPORT_CANCEL_PATH = IMPORT_PATH + "/cancel";
    private static final String IMPORT_RESULT_PATH = IMPORT_PATH + "/result";
    private static final String ATTRIBUTE_NAME = "createKeyAttribute";
    private static final String METADATA_NAME = "provider handle";
    private static final String KEY_CREATION_ID = "key-creation-1";
    private static final String KEY_IMPORT_ID = "key-import-1";
    private static final String TRANSPORT_PASSPHRASE = "8FQmS3ZbW1xkP0vTqA9rYcE4uHnJ6dLiKgOw2sXeVm0";
    private static final String VALID_ATTRIBUTE_LIST_JSON = """
            [
              {
                "uuid": "11111111-1111-1111-1111-111111111111",
                "name": "createKeyAttribute",
                "type": "data",
                "contentType": "string",
                "version": 2
              }
            ]
            """;
    private static final String VALID_METADATA_JSON = """
            {
              "uuid": "00000000-0000-0000-0000-000000000001",
              "name": "provider handle",
              "type": "meta",
              "contentType": "string",
              "version": 2,
              "properties": {},
              "content": ["provider-key-1"]
            }
            """;
    private static final String VALID_TRACKING_REQUEST_JSON = """
            {
              "operationMeta": [{
                "uuid": "00000000-0000-0000-0000-000000000001",
                "name": "provider handle",
                "version": 2,
                "type": "meta",
                "content": [{
                  "reference": "provider-key-1",
                  "data": "provider-key-1"
                }],
                "contentType": "string",
                "properties": {
                  "label": null,
                  "visible": true,
                  "group": null,
                  "global": false,
                  "overwrite": false,
                  "protectionLevel": "none"
                }
              }]
            }
            """;

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();

    // One server for the whole class: the connector WebClient pools connections per host and port for
    // 30 seconds, so a server per test would hand recycled ports back to still-pooled connections.
    private static WireMockServer mockServer;

    private KeyApiClient client;
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
        connector.setName("cryptography connector");
        connector.setUrl("http://localhost:" + mockServer.port());
        connector.setStatus(ConnectorStatus.CONNECTED);

        OperationResponseValidator responseValidator = new OperationResponseValidator(VALIDATORS.validator());
        client = new KeyApiClient(BaseApiClient.prepareWebClient(), null, responseValidator);
    }

    @Test
    void listCreateKeyAttributes_postsRequestAndReturnsAttributes() throws ConnectorException {
        // given
        stubJsonResponse(CREATE_ATTRIBUTES_PATH, HttpStatus.OK, VALID_ATTRIBUTE_LIST_JSON);

        // when
        List<BaseAttribute> result = client.listCreateKeyAttributes(connector, createKeyAttributesRequest());

        // then
        assertEquals(1, result.size());
        assertEquals(ATTRIBUTE_NAME, result.get(0).getName());
        verifyCreateAttributesRequest();
    }

    @Test
    void createKey_returnsSynchronousResponse() throws ConnectorException {
        // given
        String responseJson = """
                {
                  "keyRequestType": "secret",
                  "keyData": {"type":"Secret","algorithm":"RSA","length":2048},
                  "keyMeta": [%s]
                }
                """.formatted(VALID_METADATA_JSON);
        stubJsonResponse(BASE_PATH, HttpStatus.OK, responseJson);

        // when
        ResponseEntity<KeyCreationResponseV2Dto> result = client
                .createKey(connector, createKeyRequest(OperationExecutionMode.SYNCHRONOUS));

        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        SecretKeyDataResponseV2Dto body = assertInstanceOf(SecretKeyDataResponseV2Dto.class, result.getBody());
        assertEquals(KeyAlgorithm.RSA, body.getKeyData().getAlgorithm());
        assertEquals(METADATA_NAME, body.getKeyMeta().get(0).getName());
        verifyCreateRequest(OperationExecutionMode.SYNCHRONOUS);
    }

    @Test
    void createKey_returnsAsynchronousResponse() throws ConnectorException {
        // given
        String responseJson = """
                {"keyRequestType":"secret","operationMeta":[%s]}
                """.formatted(VALID_METADATA_JSON);
        stubJsonResponse(BASE_PATH, HttpStatus.ACCEPTED, responseJson);

        // when
        ResponseEntity<KeyCreationResponseV2Dto> result = client
                .createKey(connector, createKeyRequest(OperationExecutionMode.ASYNCHRONOUS));

        // then
        assertEquals(HttpStatus.ACCEPTED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(METADATA_NAME, result.getBody().getOperationMeta().get(0).getName());
        verifyCreateRequest(OperationExecutionMode.ASYNCHRONOUS);
    }

    @Test
    void createKey_rejectsInvalidResponse() {
        // given
        String synchronousResponseWithoutKeyData = """
                {"keyRequestType":"secret"}
                """;
        stubJsonResponse(BASE_PATH, HttpStatus.OK, synchronousResponseWithoutKeyData);

        // when
        Executable call = () -> client.createKey(connector, createKeyRequest(OperationExecutionMode.SYNCHRONOUS));

        // then
        assertValidationFailure(call);
    }

    @Test
    void createKey_rejectsMismatchedKeyRequestType() {
        // given
        String secretKeyResponse = """
                {
                  "keyRequestType": "secret",
                  "keyData": {"type":"Secret","algorithm":"RSA","length":2048},
                  "keyMeta": [%s]
                }
                """.formatted(VALID_METADATA_JSON);
        CreateKeyRequestV2Dto keyPairRequest = createKeyRequest(KeyRequestType.KEY_PAIR,
                OperationExecutionMode.SYNCHRONOUS);
        stubJsonResponse(BASE_PATH, HttpStatus.OK, secretKeyResponse);

        // when
        Executable call = () -> client.createKey(connector, keyPairRequest);

        // then
        assertValidationFailure(call);
    }

    @Test
    void getCreateKeyStatus_postsRequestAndReturnsStatus() throws ConnectorException {
        // given
        String responseJson = """
                {"keyRequestType":"secret","status":"inProgress"}
                """;
        stubJsonResponse(CREATE_STATUS_PATH, HttpStatus.OK, responseJson);

        // when
        KeyCreationStatusResponseV2Dto result = client.getCreateKeyStatus(connector, keyOperationRequest());

        // then
        assertInstanceOf(SecretKeyOperationStatusResponseV2Dto.class, result);
        assertEquals(OperationStatus.IN_PROGRESS, result.getStatus());
        verifyOperationRequest(CREATE_STATUS_PATH);
    }

    @Test
    void getCreateKeyStatus_rejectsInvalidResponse() {
        // given
        String responseWithoutStatus = """
                {"keyRequestType":"secret"}
                """;
        stubJsonResponse(CREATE_STATUS_PATH, HttpStatus.OK, responseWithoutStatus);

        // when
        Executable call = () -> client.getCreateKeyStatus(connector, keyOperationRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void cancelCreateKey_postsRequestAndPreservesStatus() throws ConnectorException {
        // given
        HttpStatus connectorStatus = HttpStatus.NO_CONTENT;
        stubBodilessResponse(CREATE_CANCEL_PATH, connectorStatus);

        // when
        ResponseEntity<Void> result = client.cancelCreateKey(connector, keyOperationRequest());

        // then
        assertEquals(connectorStatus, result.getStatusCode());
        verifyOperationRequest(CREATE_CANCEL_PATH);
    }

    @Test
    void destroyKey_returnsSynchronousResponse() throws ConnectorException {
        // given
        String responseJson = "{}";
        stubJsonResponse(DESTROY_PATH, HttpStatus.OK, responseJson);

        // when
        ResponseEntity<KeyOperationResponseV2Dto> result = client
                .destroyKey(connector, destroyKeyRequest(OperationExecutionMode.SYNCHRONOUS));

        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        verifyDestroyRequest(OperationExecutionMode.SYNCHRONOUS);
    }

    @Test
    void destroyKey_returnsAsynchronousResponse() throws ConnectorException {
        // given
        String responseJson = """
                {"operationMeta":[%s]}
                """.formatted(VALID_METADATA_JSON);
        stubJsonResponse(DESTROY_PATH, HttpStatus.ACCEPTED, responseJson);

        // when
        ResponseEntity<KeyOperationResponseV2Dto> result = client
                .destroyKey(connector, destroyKeyRequest(OperationExecutionMode.ASYNCHRONOUS));

        // then
        assertEquals(HttpStatus.ACCEPTED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(METADATA_NAME, result.getBody().getOperationMeta().get(0).getName());
        verifyDestroyRequest(OperationExecutionMode.ASYNCHRONOUS);
    }

    @Test
    void destroyKey_rejectsInvalidResponse() {
        // given
        String asynchronousResponseWithoutOperationMetadata = "{}";
        stubJsonResponse(DESTROY_PATH, HttpStatus.ACCEPTED, asynchronousResponseWithoutOperationMetadata);

        // when
        Executable call = () -> client.destroyKey(connector, destroyKeyRequest(OperationExecutionMode.ASYNCHRONOUS));

        // then
        assertValidationFailure(call);
    }

    @Test
    void getDestroyKeyStatus_postsRequestAndReturnsStatus() throws ConnectorException {
        // given
        String responseJson = """
                {"status":"inProgress"}
                """;
        stubJsonResponse(DESTROY_STATUS_PATH, HttpStatus.OK, responseJson);

        // when
        KeyOperationStatusResponseV2Dto result = client.getDestroyKeyStatus(connector, keyOperationRequest());

        // then
        assertInstanceOf(KeyDestructionStatusResponseV2Dto.class, result);
        assertEquals(OperationStatus.IN_PROGRESS, result.getStatus());
        verifyOperationRequest(DESTROY_STATUS_PATH);
    }

    @Test
    void getDestroyKeyStatus_rejectsInvalidResponse() {
        // given
        String responseWithoutStatus = "{}";
        stubJsonResponse(DESTROY_STATUS_PATH, HttpStatus.OK, responseWithoutStatus);

        // when
        Executable call = () -> client.getDestroyKeyStatus(connector, keyOperationRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void cancelDestroyKey_postsRequestAndPreservesStatus() throws ConnectorException {
        // given
        HttpStatus connectorStatus = HttpStatus.NO_CONTENT;
        stubBodilessResponse(DESTROY_CANCEL_PATH, connectorStatus);

        // when
        ResponseEntity<Void> result = client.cancelDestroyKey(connector, keyOperationRequest());

        // then
        assertEquals(connectorStatus, result.getStatusCode());
        verifyOperationRequest(DESTROY_CANCEL_PATH);
    }

    @Test
    void createKey_propagatesConnectorHttpError() {
        // given
        HttpStatus connectorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String connectorError = "connector failure";
        mockServer
                .stubFor(WireMock
                        .post(BASE_PATH)
                        .willReturn(WireMock.aResponse().withStatus(connectorStatus.value()).withBody(connectorError)));

        // when
        Executable call = () -> client.createKey(connector, createKeyRequest(OperationExecutionMode.SYNCHRONOUS));

        // then
        ConnectorServerException exception = assertThrows(ConnectorServerException.class, call);
        assertEquals(connectorStatus, exception.getHttpStatus());
        assertSame(connector, exception.getConnector());
    }

    @Test
    void listImportableKeyTypes_postsRequestAndReturnsTypes() throws ConnectorException {
        // given
        String responseJson = """
                [{"keyRequestType":"keyPair","algorithms":["RSA"]}]
                """;
        stubJsonResponse(IMPORT_KEY_TYPES_PATH, HttpStatus.OK, responseJson);

        // when
        List<ImportableKeyTypeV2Dto> result = client
                .listImportableKeyTypes(connector, withValidTokenProfileScope(new TokenProfileScopedRequestV2Dto()));

        // then
        assertEquals(1, result.size());
        assertEquals(KeyRequestType.KEY_PAIR, result.get(0).getKeyRequestType());
        assertEquals(Set.of(KeyAlgorithm.RSA), result.get(0).getAlgorithms());
        mockServer.verify(tokenProfileRequest(IMPORT_KEY_TYPES_PATH));
    }

    @Test
    void listImportableKeyTypes_rejectsTypeWithoutAlgorithms() {
        // given
        String responseJson = """
                [{"keyRequestType":"keyPair"}]
                """;
        stubJsonResponse(IMPORT_KEY_TYPES_PATH, HttpStatus.OK, responseJson);

        // when
        Executable call = () -> client
                .listImportableKeyTypes(connector, withValidTokenProfileScope(new TokenProfileScopedRequestV2Dto()));

        // then
        assertValidationFailure(call);
    }

    @Test
    void listImportKeyAttributes_postsRequestAndReturnsAttributes() throws ConnectorException {
        // given
        stubJsonResponse(IMPORT_ATTRIBUTES_PATH, HttpStatus.OK, VALID_ATTRIBUTE_LIST_JSON);

        // when
        List<BaseAttribute> result = client.listImportKeyAttributes(connector, importKeyAttributesRequest());

        // then
        assertEquals(1, result.size());
        assertEquals(ATTRIBUTE_NAME, result.get(0).getName());
        RequestPatternBuilder request = tokenProfileRequest(IMPORT_ATTRIBUTES_PATH)
                .withRequestBody(WireMock.matchingJsonPath("$.keyRequestType", WireMock.equalTo("secret")));
        mockServer.verify(request);
    }

    @Test
    void importKey_returnsSynchronousResponse() throws ConnectorException {
        // given
        String responseJson = """
                {
                  "keyRequestType": "secret",
                  "keyData": {"type":"Secret","algorithm":"RSA","length":2048},
                  "keyMeta": [%s]
                }
                """.formatted(VALID_METADATA_JSON);
        stubJsonResponse(IMPORT_PATH, HttpStatus.OK, responseJson);

        // when
        ResponseEntity<KeyCreationResponseV2Dto> result = client
                .importKey(connector, importKeyRequest(OperationExecutionMode.SYNCHRONOUS));

        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        SecretKeyDataResponseV2Dto body = assertInstanceOf(SecretKeyDataResponseV2Dto.class, result.getBody());
        assertEquals(KeyAlgorithm.RSA, body.getKeyData().getAlgorithm());
        verifyImportRequest(OperationExecutionMode.SYNCHRONOUS);
    }

    @Test
    void importKey_returnsAsynchronousResponse() throws ConnectorException {
        // given
        String responseJson = """
                {"keyRequestType":"secret","operationMeta":[%s]}
                """.formatted(VALID_METADATA_JSON);
        stubJsonResponse(IMPORT_PATH, HttpStatus.ACCEPTED, responseJson);

        // when
        ResponseEntity<KeyCreationResponseV2Dto> result = client
                .importKey(connector, importKeyRequest(OperationExecutionMode.ASYNCHRONOUS));

        // then
        assertEquals(HttpStatus.ACCEPTED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(METADATA_NAME, result.getBody().getOperationMeta().get(0).getName());
        verifyImportRequest(OperationExecutionMode.ASYNCHRONOUS);
    }

    @Test
    void importKey_sendsProtectedMaterialAndTransportPassphrase() throws ConnectorException {
        // given
        String responseJson = """
                {"keyRequestType":"secret","operationMeta":[%s]}
                """.formatted(VALID_METADATA_JSON);
        stubJsonResponse(IMPORT_PATH, HttpStatus.ACCEPTED, responseJson);

        // when
        client.importKey(connector, importKeyRequest(OperationExecutionMode.ASYNCHRONOUS));

        // then
        String expectedEnvelope = Base64.getEncoder().encodeToString(pinnedProtectionEnvelope());
        RequestPatternBuilder request = WireMock
                .postRequestedFor(WireMock.urlEqualTo(IMPORT_PATH))
                .withRequestBody(WireMock
                        .matchingJsonPath("$.material.encryptedPrivateKeyInfo", WireMock.equalTo(expectedEnvelope)))
                .withRequestBody(WireMock.matchingJsonPath("$.passphrase", WireMock.equalTo(TRANSPORT_PASSPHRASE)));
        mockServer.verify(request);
    }

    @Test
    void importKey_rejectsInvalidResponse() {
        // given
        String synchronousResponseWithoutKeyData = """
                {"keyRequestType":"secret"}
                """;
        stubJsonResponse(IMPORT_PATH, HttpStatus.OK, synchronousResponseWithoutKeyData);

        // when
        Executable call = () -> client.importKey(connector, importKeyRequest(OperationExecutionMode.SYNCHRONOUS));

        // then
        assertValidationFailure(call);
    }

    @Test
    void getImportKeyStatus_postsRequestAndReturnsStatus() throws ConnectorException {
        // given
        String responseJson = """
                {"keyRequestType":"secret","status":"inProgress"}
                """;
        stubJsonResponse(IMPORT_STATUS_PATH, HttpStatus.OK, responseJson);

        // when
        KeyCreationStatusResponseV2Dto result = client.getImportKeyStatus(connector, keyOperationRequest());

        // then
        assertInstanceOf(SecretKeyOperationStatusResponseV2Dto.class, result);
        assertEquals(OperationStatus.IN_PROGRESS, result.getStatus());
        verifyOperationRequest(IMPORT_STATUS_PATH);
    }

    @Test
    void getImportKeyStatus_rejectsInvalidResponse() {
        // given
        String responseWithoutStatus = """
                {"keyRequestType":"secret"}
                """;
        stubJsonResponse(IMPORT_STATUS_PATH, HttpStatus.OK, responseWithoutStatus);

        // when
        Executable call = () -> client.getImportKeyStatus(connector, keyOperationRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void cancelImportKey_postsRequestAndPreservesStatus() throws ConnectorException {
        // given
        HttpStatus connectorStatus = HttpStatus.NO_CONTENT;
        stubBodilessResponse(IMPORT_CANCEL_PATH, connectorStatus);

        // when
        ResponseEntity<Void> result = client.cancelImportKey(connector, keyOperationRequest());

        // then
        assertEquals(connectorStatus, result.getStatusCode());
        verifyOperationRequest(IMPORT_CANCEL_PATH);
    }

    @Test
    void getImportKeyResult_postsRequestAndReturnsRecordedOutcome() throws ConnectorException {
        // given
        String responseJson = """
                {
                  "keyRequestType": "secret",
                  "status": "completed",
                  "result": {
                    "keyRequestType": "secret",
                    "keyData": {"type":"Secret","algorithm":"RSA","length":2048},
                    "keyMeta": [%s]
                  }
                }
                """.formatted(VALID_METADATA_JSON);
        stubJsonResponse(IMPORT_RESULT_PATH, HttpStatus.OK, responseJson);

        // when
        KeyCreationStatusResponseV2Dto result = client.getImportKeyResult(connector, importKeyResultRequest());

        // then
        assertEquals(OperationStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getResult());
        RequestPatternBuilder request = WireMock
                .postRequestedFor(WireMock.urlEqualTo(IMPORT_RESULT_PATH))
                .withRequestBody(WireMock.matchingJsonPath("$.keyImportId", WireMock.equalTo(KEY_IMPORT_ID)));
        mockServer.verify(request);
    }

    @Test
    void listExportableKeyTypes_postsRequestAndReturnsTypes() throws ConnectorException {
        // given
        String responseJson = """
                [{"keyRequestType":"keyPair","algorithms":["RSA"]}]
                """;
        stubJsonResponse(EXPORT_KEY_TYPES_PATH, HttpStatus.OK, responseJson);

        // when
        List<ExportableKeyTypeV2Dto> result = client
                .listExportableKeyTypes(connector, withValidTokenProfileScope(new TokenProfileScopedRequestV2Dto()));

        // then
        assertEquals(1, result.size());
        assertEquals(KeyRequestType.KEY_PAIR, result.get(0).getKeyRequestType());
        assertEquals(Set.of(KeyAlgorithm.RSA), result.get(0).getAlgorithms());
        mockServer.verify(tokenProfileRequest(EXPORT_KEY_TYPES_PATH));
    }

    @Test
    void listExportableKeyTypes_rejectsTypeWithoutAlgorithms() {
        // given
        String responseJson = """
                [{"keyRequestType":"keyPair"}]
                """;
        stubJsonResponse(EXPORT_KEY_TYPES_PATH, HttpStatus.OK, responseJson);

        // when
        Executable call = () -> client
                .listExportableKeyTypes(connector, withValidTokenProfileScope(new TokenProfileScopedRequestV2Dto()));

        // then
        assertValidationFailure(call);
    }

    @Test
    void listExportKeyAttributes_postsRequestAndReturnsAttributes() throws ConnectorException {
        // given
        stubJsonResponse(EXPORT_ATTRIBUTES_PATH, HttpStatus.OK, VALID_ATTRIBUTE_LIST_JSON);

        // when
        List<BaseAttribute> result = client.listExportKeyAttributes(connector, keyScopedRequest());

        // then
        assertEquals(1, result.size());
        assertEquals(ATTRIBUTE_NAME, result.get(0).getName());
        RequestPatternBuilder request = tokenProfileRequest(EXPORT_ATTRIBUTES_PATH)
                .withRequestBody(WireMock.matchingJsonPath("$.keyMeta[0].name", WireMock.equalTo(METADATA_NAME)));
        mockServer.verify(request);
    }

    @Test
    void exportKey_returnsProtectedMaterialAndTheKeyDescriptor() throws ConnectorException {
        // given
        stubJsonResponse(EXPORT_PATH, HttpStatus.OK, exportResponseJson());

        // when
        ExportKeyResponseV2Dto result = client.exportKey(connector, validExportKeyRequest());

        // then
        assertNotNull(result.getMaterial());
        assertEquals(KEY_REFERENCE, result.getKeyReference());
        assertEquals(KeyAlgorithm.RSA, result.getKeyData().getAlgorithm());
        verifyExportRequest();
    }

    @Test
    void exportKey_sendsThePassphraseAndKeepsCertificatesOut() throws ConnectorException {
        // given
        stubJsonResponse(EXPORT_PATH, HttpStatus.OK, exportResponseJson());

        // when
        client.exportKey(connector, validExportKeyRequest());

        // then
        RequestPatternBuilder request = WireMock
                .postRequestedFor(WireMock.urlEqualTo(EXPORT_PATH))
                .withRequestBody(WireMock.matchingJsonPath("$.passphrase", WireMock.equalTo(EXPORT_PASSPHRASE)))
                .withRequestBody(WireMock.matchingJsonPath("$[?(!@.certificates)]"));
        mockServer.verify(request);
    }

    @Test
    void exportKey_rejectsAnAsynchronousResponse() {
        // given
        stubJsonResponse(EXPORT_PATH, HttpStatus.ACCEPTED, exportResponseJson());

        // when
        Executable call = () -> client.exportKey(connector, validExportKeyRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void exportKey_rejectsResponseWithoutTheEchoedKeyReference() {
        // given
        String responseWithoutReference = """
                {
                  "material": {"encryptedPrivateKeyInfo": "%s"},
                  "keyData": {"type":"Public","algorithm":"RSA","length":2048,"publicKeySpki":"%s"}
                }
                """.formatted(envelopeBase64(), publicKeySpkiBase64());
        stubJsonResponse(EXPORT_PATH, HttpStatus.OK, responseWithoutReference);

        // when
        Executable call = () -> client.exportKey(connector, validExportKeyRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void exportKey_rejectsResponseWithoutTheKeyDescriptor() {
        // given
        String responseWithoutKeyData = """
                {
                  "material": {"encryptedPrivateKeyInfo": "%s"},
                  "keyReference": "%s"
                }
                """.formatted(envelopeBase64(), KEY_REFERENCE);
        stubJsonResponse(EXPORT_PATH, HttpStatus.OK, responseWithoutKeyData);

        // when
        Executable call = () -> client.exportKey(connector, validExportKeyRequest());

        // then
        assertValidationFailure(call);
    }

    private void verifyExportRequest() {
        RequestPatternBuilder request = tokenProfileRequest(EXPORT_PATH)
                .withRequestBody(WireMock.matchingJsonPath("$.keyRequestType", WireMock.equalTo("keyPair")))
                .withRequestBody(WireMock.matchingJsonPath("$.keyReference", WireMock.equalTo(KEY_REFERENCE)))
                .withRequestBody(WireMock.matchingJsonPath("$.keyMeta[0].name", WireMock.equalTo(METADATA_NAME)))
                .withRequestBody(WireMock.equalToJson("""
                        {"exportKeyAttributes": []}
                        """, true, true));
        mockServer.verify(request);
    }

    private static String exportResponseJson() {
        return """
                {
                  "material": {"encryptedPrivateKeyInfo": "%s"},
                  "keyReference": "%s",
                  "keyData": {"type":"Public","algorithm":"RSA","length":2048,"publicKeySpki":"%s"}
                }
                """.formatted(envelopeBase64(), KEY_REFERENCE, publicKeySpkiBase64());
    }

    private static String envelopeBase64() {
        return Base64.getEncoder().encodeToString(pinnedProtectionEnvelope());
    }

    private static String publicKeySpkiBase64() {
        return Base64.getEncoder().encodeToString(validPublicKeyData().getPublicKeySpki());
    }

    private static KeyScopedRequestV2Dto keyScopedRequest() {
        KeyScopedRequestV2Dto request = withValidTokenProfileScope(new KeyScopedRequestV2Dto());
        request.setKeyMeta(validMetadata());
        return request;
    }

    private void verifyImportRequest(OperationExecutionMode mode) {
        RequestPatternBuilder request = tokenProfileRequest(IMPORT_PATH)
                .withRequestBody(WireMock.matchingJsonPath("$.keyRequestType", WireMock.equalTo("secret")))
                .withRequestBody(WireMock.matchingJsonPath("$.executionMode", WireMock.equalTo(mode.getCode())))
                .withRequestBody(WireMock.matchingJsonPath("$.keyImportId", WireMock.equalTo(KEY_IMPORT_ID)))
                .withRequestBody(WireMock.matchingJsonPath("$.keyReference", WireMock.equalTo(KEY_REFERENCE)))
                .withRequestBody(WireMock.matchingJsonPath("$.exportable", WireMock.equalTo("false")))
                .withRequestBody(WireMock.equalToJson("""
                        {"importKeyAttributes": []}
                        """, true, true));
        mockServer.verify(request);
    }

    private void verifyCreateAttributesRequest() {
        RequestPatternBuilder request = tokenProfileRequest(CREATE_ATTRIBUTES_PATH)
                .withRequestBody(WireMock.matchingJsonPath("$.keyRequestType", WireMock.equalTo("secret")));
        mockServer.verify(request);
    }

    private void verifyCreateRequest(OperationExecutionMode mode) {
        RequestPatternBuilder request = tokenProfileRequest(BASE_PATH)
                .withRequestBody(WireMock.matchingJsonPath("$.keyRequestType", WireMock.equalTo("secret")))
                .withRequestBody(WireMock.matchingJsonPath("$.executionMode", WireMock.equalTo(mode.getCode())))
                .withRequestBody(WireMock.matchingJsonPath("$.keyCreationId", WireMock.equalTo(KEY_CREATION_ID)))
                .withRequestBody(WireMock.equalToJson("""
                        {"createKeyAttributes": []}
                        """, true, true));
        mockServer.verify(request);
    }

    private void verifyDestroyRequest(OperationExecutionMode mode) {
        RequestPatternBuilder request = tokenProfileRequest(DESTROY_PATH)
                .withRequestBody(WireMock.matchingJsonPath("$.executionMode", WireMock.equalTo(mode.getCode())))
                .withRequestBody(WireMock.matchingJsonPath("$.keyMeta[0].name", WireMock.equalTo(METADATA_NAME)));
        mockServer.verify(request);
    }

    private void verifyOperationRequest(String path) {
        boolean ignoreArrayOrder = false;
        boolean ignoreExtraElements = false;
        RequestPatternBuilder request = WireMock
                .postRequestedFor(WireMock.urlEqualTo(path))
                .withRequestBody(
                        WireMock.equalToJson(VALID_TRACKING_REQUEST_JSON, ignoreArrayOrder, ignoreExtraElements));
        mockServer.verify(request);
    }

    private RequestPatternBuilder tokenProfileRequest(String path) {
        return WireMock.postRequestedFor(WireMock.urlEqualTo(path)).withRequestBody(WireMock.equalToJson("""
                {
                  "tokenAttributes": [],
                  "tokenProfileAttributes": []
                }
                """, true, true));
    }

    private void stubJsonResponse(String path, HttpStatus status, String body) {
        mockServer
                .stubFor(WireMock
                        .post(path)
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(status.value())
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(body)));
    }

    private void stubBodilessResponse(String path, HttpStatus status) {
        mockServer.stubFor(WireMock.post(path).willReturn(WireMock.aResponse().withStatus(status.value())));
    }

    private void assertValidationFailure(Executable call) {
        ConnectorException exception = assertThrows(ConnectorException.class, call);
        assertInstanceOf(IllegalArgumentException.class, exception.getCause(),
                () -> "expected a response-validation failure, got " + exception);
        assertSame(connector, exception.getConnector());
    }

    private static CreateKeyAttributesRequestV2Dto createKeyAttributesRequest() {
        CreateKeyAttributesRequestV2Dto request = withValidTokenProfileScope(new CreateKeyAttributesRequestV2Dto());
        request.setKeyRequestType(KeyRequestType.SECRET);
        return request;
    }

    private static CreateKeyRequestV2Dto createKeyRequest(OperationExecutionMode mode) {
        return createKeyRequest(KeyRequestType.SECRET, mode);
    }

    private static CreateKeyRequestV2Dto createKeyRequest(KeyRequestType keyRequestType, OperationExecutionMode mode) {
        CreateKeyRequestV2Dto request = withValidTokenProfileScope(new CreateKeyRequestV2Dto());
        request.setKeyRequestType(keyRequestType);
        request.setExecutionMode(mode);
        request.setKeyCreationId(KEY_CREATION_ID);
        request.setCreateKeyAttributes(List.of());
        return request;
    }

    private static DestroyKeyRequestV2Dto destroyKeyRequest(OperationExecutionMode mode) {
        DestroyKeyRequestV2Dto request = withValidTokenProfileScope(new DestroyKeyRequestV2Dto());
        request.setKeyMeta(validMetadata());
        request.setExecutionMode(mode);
        return request;
    }

    private static ImportKeyAttributesRequestV2Dto importKeyAttributesRequest() {
        ImportKeyAttributesRequestV2Dto request = withValidTokenProfileScope(new ImportKeyAttributesRequestV2Dto());
        request.setKeyRequestType(KeyRequestType.SECRET);
        return request;
    }

    private static ImportKeyRequestV2Dto importKeyRequest(OperationExecutionMode mode) {
        ImportKeyRequestV2Dto request = withValidTokenProfileScope(new ImportKeyRequestV2Dto());
        request.setKeyRequestType(KeyRequestType.SECRET);
        request.setExecutionMode(mode);
        request.setKeyImportId(KEY_IMPORT_ID);
        request.setKeyReference(KEY_REFERENCE);
        request.setImportKeyAttributes(List.of());
        request.setMaterial(validEncryptedKeyMaterial());
        request.setPassphrase(TRANSPORT_PASSPHRASE);
        request.setExportable(Boolean.FALSE);
        return request;
    }

    private static ImportKeyResultRequestV2Dto importKeyResultRequest() {
        ImportKeyResultRequestV2Dto request = withValidTokenScope(new ImportKeyResultRequestV2Dto());
        request.setKeyImportId(KEY_IMPORT_ID);
        return request;
    }

    private static OperationTrackingRequestV2Dto keyOperationRequest() {
        OperationTrackingRequestV2Dto request = new OperationTrackingRequestV2Dto();
        request.setOperationMeta(validMetadata());
        return request;
    }
}
