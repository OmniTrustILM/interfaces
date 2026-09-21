package com.otilm.api.clients.v2;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.otilm.api.clients.BaseApiClient;
import com.otilm.api.clients.cryptography.v2.CryptographicOperationsApiClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.ConnectorServerException;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.common.v2.OperationStatus;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.OperationResponseValidator;
import com.otilm.api.model.connector.cryptography.v2.OperationTrackingRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.CipherDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.DecryptDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.EncryptDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.RandomDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.RandomDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignOperationStatusResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.VerifyDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.VerifyDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.data.CipherDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.data.SignatureDataV2Dto;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.model.core.connector.ConnectorStatus;
import com.otilm.api.testsupport.ValidatorFixture;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadata;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.withValidTokenProfileScope;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Named.named;

class CryptographicOperationsApiClientTest {

    private static final String BASE_PATH = "/v2/cryptographyProvider/operations";
    private static final String ENCRYPT_PATH = BASE_PATH + "/encrypt";
    private static final String DECRYPT_PATH = BASE_PATH + "/decrypt";
    private static final String SIGN_PATH = BASE_PATH + "/sign";
    private static final String SIGN_STATUS_PATH = SIGN_PATH + "/status";
    private static final String SIGN_CANCEL_PATH = SIGN_PATH + "/cancel";
    private static final String VERIFY_PATH = BASE_PATH + "/verify";
    private static final String RANDOM_PATH = BASE_PATH + "/random";
    private static final String ITEM_IDENTIFIER = "item-1";
    private static final String DIFFERENT_ITEM_IDENTIFIER = "different-item";
    private static final byte[] ITEM_DATA = {1};
    private static final String VALID_ATTRIBUTE_LIST_JSON = """
            [
              {
                "uuid": "11111111-1111-1111-1111-111111111111",
                "name": "operationAttribute",
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

    private CryptographicOperationsApiClient client;
    private ConnectorDto connector;
    private WireMockServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = new WireMockServer(options().dynamicPort());
        mockServer.start();
        WireMock.configureFor("localhost", mockServer.port());

        connector = new ConnectorDto();
        connector.setName("cryptography connector");
        connector.setUrl("http://localhost:" + mockServer.port());
        connector.setStatus(ConnectorStatus.CONNECTED);

        OperationResponseValidator responseValidator = new OperationResponseValidator(VALIDATORS.validator());
        client = new CryptographicOperationsApiClient(BaseApiClient.prepareWebClient(), null, responseValidator);
    }

    @AfterEach
    void tearDown() {
        mockServer.stop();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("attributeOperations")
    void attributeOperation_postsRequestAndReturnsAttributes(AttributeOperation operation) throws ConnectorException {
        // given
        stubJsonResponse(operation.path(), HttpStatus.OK, VALID_ATTRIBUTE_LIST_JSON);

        // when
        List<BaseAttribute> result = invokeAttributeOperation(operation);

        // then
        assertEquals(1, result.size());
        assertEquals("operationAttribute", result.get(0).getName());
        verifyAttributeRequest(operation);
    }

    @Test
    void encryptData_postsRequestAndReturnsEncryptedData() throws ConnectorException {
        // given
        String responseJson = """
                {"encryptedData":[{"data":"AQ==","identifier":"item-1"}]}
                """;
        stubJsonResponse(ENCRYPT_PATH, HttpStatus.OK, responseJson);

        // when
        EncryptDataResponseV2Dto result = client.encryptData(connector, cipherRequest());

        // then
        assertEquals(ITEM_IDENTIFIER, result.getEncryptedData().get(0).getIdentifier());
        assertArrayEquals(ITEM_DATA, result.getEncryptedData().get(0).getData());
        verifyIdentifierRequest(ENCRYPT_PATH, "cipherData");
    }

    @Test
    void encryptData_rejectsInvalidResponse() {
        // given
        String responseWithoutEncryptedData = "{}";
        stubJsonResponse(ENCRYPT_PATH, HttpStatus.OK, responseWithoutEncryptedData);

        // when
        Executable call = () -> client.encryptData(connector, cipherRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void encryptData_rejectsResponseWithDifferentIdentifier() {
        // given
        String responseWithDifferentIdentifier = """
                {"encryptedData":[{"data":"AQ==","identifier":"%s"}]}
                """.formatted(DIFFERENT_ITEM_IDENTIFIER);
        stubJsonResponse(ENCRYPT_PATH, HttpStatus.OK, responseWithDifferentIdentifier);

        // when
        Executable call = () -> client.encryptData(connector, cipherRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void decryptData_postsRequestAndReturnsDecryptedData() throws ConnectorException {
        // given
        String responseJson = """
                {"decryptedData":[{"data":"AQ==","identifier":"item-1"}]}
                """;
        stubJsonResponse(DECRYPT_PATH, HttpStatus.OK, responseJson);

        // when
        DecryptDataResponseV2Dto result = client.decryptData(connector, cipherRequest());

        // then
        assertEquals(ITEM_IDENTIFIER, result.getDecryptedData().get(0).getIdentifier());
        assertArrayEquals(ITEM_DATA, result.getDecryptedData().get(0).getData());
        verifyIdentifierRequest(DECRYPT_PATH, "cipherData");
    }

    @Test
    void decryptData_rejectsInvalidResponse() {
        // given
        String responseWithoutDecryptedData = "{}";
        stubJsonResponse(DECRYPT_PATH, HttpStatus.OK, responseWithoutDecryptedData);

        // when
        Executable call = () -> client.decryptData(connector, cipherRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void decryptData_rejectsResponseWithDifferentIdentifier() {
        // given
        String responseWithDifferentIdentifier = """
                {"decryptedData":[{"data":"AQ==","identifier":"%s"}]}
                """.formatted(DIFFERENT_ITEM_IDENTIFIER);
        stubJsonResponse(DECRYPT_PATH, HttpStatus.OK, responseWithDifferentIdentifier);

        // when
        Executable call = () -> client.decryptData(connector, cipherRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void signData_returnsSynchronousResponse() throws ConnectorException {
        // given
        String responseJson = """
                {"signatures":[{"data":"AQ==","identifier":"item-1"}]}
                """;
        stubJsonResponse(SIGN_PATH, HttpStatus.OK, responseJson);

        // when
        ResponseEntity<SignDataResponseV2Dto> result = client
                .signData(connector, signRequest(OperationExecutionMode.SYNCHRONOUS));

        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(ITEM_IDENTIFIER, result.getBody().getSignatures().get(0).getIdentifier());
        verifySignRequest(OperationExecutionMode.SYNCHRONOUS);
    }

    @Test
    void signData_returnsAsynchronousResponse() throws ConnectorException {
        // given
        String responseJson = """
                {"operationMeta":[%s]}
                """.formatted(VALID_METADATA_JSON);
        stubJsonResponse(SIGN_PATH, HttpStatus.ACCEPTED, responseJson);

        // when
        ResponseEntity<SignDataResponseV2Dto> result = client
                .signData(connector, signRequest(OperationExecutionMode.ASYNCHRONOUS));

        // then
        assertEquals(HttpStatus.ACCEPTED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("provider handle", result.getBody().getOperationMeta().get(0).getName());
        verifySignRequest(OperationExecutionMode.ASYNCHRONOUS);
    }

    @Test
    void signData_rejectsInvalidResponse() {
        // given
        String responseWithoutSignatures = "{}";
        stubJsonResponse(SIGN_PATH, HttpStatus.OK, responseWithoutSignatures);

        // when
        Executable call = () -> client.signData(connector, signRequest(OperationExecutionMode.SYNCHRONOUS));

        // then
        assertValidationFailure(call);
    }

    @Test
    void getSignStatus_postsRequestAndReturnsStatus() throws ConnectorException {
        // given
        String responseJson = """
                {"items":[{"identifier":"item-1","status":"completed","signature":"AQ=="}]}
                """;
        stubJsonResponse(SIGN_STATUS_PATH, HttpStatus.OK, responseJson);

        // when
        SignOperationStatusResponseV2Dto result = client.getSignStatus(connector, signOperationRequest());

        // then
        assertEquals(ITEM_IDENTIFIER, result.getItems().get(0).getIdentifier());
        assertEquals(OperationStatus.COMPLETED, result.getItems().get(0).getStatus());
        verifyOperationMetadataRequest(SIGN_STATUS_PATH);
    }

    @Test
    void getSignStatus_rejectsInvalidResponse() {
        // given
        String responseWithoutItems = "{}";
        stubJsonResponse(SIGN_STATUS_PATH, HttpStatus.OK, responseWithoutItems);

        // when
        Executable call = () -> client.getSignStatus(connector, signOperationRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void cancelSign_postsRequestAndPreservesStatus() throws ConnectorException {
        // given
        HttpStatus connectorStatus = HttpStatus.NO_CONTENT;
        mockServer
                .stubFor(WireMock
                        .post(SIGN_CANCEL_PATH)
                        .willReturn(WireMock.aResponse().withStatus(connectorStatus.value())));

        // when
        ResponseEntity<Void> result = client.cancelSign(connector, signOperationRequest());

        // then
        assertEquals(connectorStatus, result.getStatusCode());
        verifyOperationMetadataRequest(SIGN_CANCEL_PATH);
    }

    @Test
    void verifyData_postsRequestAndReturnsVerifications() throws ConnectorException {
        // given
        String responseJson = """
                {"verifications":[{"result":true,"identifier":"item-1"}]}
                """;
        stubJsonResponse(VERIFY_PATH, HttpStatus.OK, responseJson);

        // when
        VerifyDataResponseV2Dto result = client.verifyData(connector, verifyRequest());

        // then
        assertEquals(ITEM_IDENTIFIER, result.getVerifications().get(0).getIdentifier());
        assertEquals(Boolean.TRUE, result.getVerifications().get(0).getResult());
        verifyIdentifierRequest(VERIFY_PATH, "signatures");
    }

    @Test
    void verifyData_rejectsInvalidResponse() {
        // given
        String responseWithoutVerifications = "{}";
        stubJsonResponse(VERIFY_PATH, HttpStatus.OK, responseWithoutVerifications);

        // when
        Executable call = () -> client.verifyData(connector, verifyRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void verifyData_rejectsResponseWithDifferentIdentifier() {
        // given
        String responseWithDifferentIdentifier = """
                {"verifications":[{"result":true,"identifier":"%s"}]}
                """.formatted(DIFFERENT_ITEM_IDENTIFIER);
        stubJsonResponse(VERIFY_PATH, HttpStatus.OK, responseWithDifferentIdentifier);

        // when
        Executable call = () -> client.verifyData(connector, verifyRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void randomData_postsRequestAndReturnsRandomData() throws ConnectorException {
        // given
        String responseJson = """
                {"data":"AQ=="}
                """;
        stubJsonResponse(RANDOM_PATH, HttpStatus.OK, responseJson);

        // when
        RandomDataResponseV2Dto result = client.randomData(connector, randomRequest());

        // then
        assertArrayEquals(ITEM_DATA, result.getData());
        mockServer
                .verify(WireMock
                        .postRequestedFor(WireMock.urlEqualTo(RANDOM_PATH))
                        .withRequestBody(WireMock.matchingJsonPath("$.length", WireMock.equalTo("1"))));
    }

    @Test
    void randomData_rejectsInvalidResponse() {
        // given
        String responseWithoutData = "{}";
        stubJsonResponse(RANDOM_PATH, HttpStatus.OK, responseWithoutData);

        // when
        Executable call = () -> client.randomData(connector, randomRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void randomData_rejectsResponseWithDifferentLength() {
        // given
        String twoByteResponse = """
                {"data":"AQI="}
                """;
        stubJsonResponse(RANDOM_PATH, HttpStatus.OK, twoByteResponse);

        // when
        Executable call = () -> client.randomData(connector, randomRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void encryptData_propagatesConnectorHttpError() {
        // given
        HttpStatus connectorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String connectorError = "connector failure";
        mockServer
                .stubFor(WireMock
                        .post(ENCRYPT_PATH)
                        .willReturn(WireMock.aResponse().withStatus(connectorStatus.value()).withBody(connectorError)));

        // when
        Executable call = () -> client.encryptData(connector, cipherRequest());

        // then
        ConnectorServerException exception = assertThrows(ConnectorServerException.class, call);
        assertEquals(connectorStatus, exception.getHttpStatus());
        assertSame(connector, exception.getConnector());
    }

    static Stream<Named<AttributeOperation>> attributeOperations() {
        return Stream
                .of(named("encrypt attributes", AttributeOperation.ENCRYPT),
                        named("decrypt attributes", AttributeOperation.DECRYPT),
                        named("sign attributes", AttributeOperation.SIGN),
                        named("verify attributes", AttributeOperation.VERIFY),
                        named("random attributes", AttributeOperation.RANDOM));
    }

    private List<BaseAttribute> invokeAttributeOperation(AttributeOperation operation) throws ConnectorException {
        return switch (operation) {
            case ENCRYPT -> client.listEncryptAttributes(connector, keyScopedRequest());
            case DECRYPT -> client.listDecryptAttributes(connector, keyScopedRequest());
            case SIGN -> client.listSignAttributes(connector, keyScopedRequest());
            case VERIFY -> client.listVerifyAttributes(connector, keyScopedRequest());
            case RANDOM -> client.listRandomAttributes(connector, tokenProfileScopedRequest());
        };
    }

    private void verifyAttributeRequest(AttributeOperation operation) {
        RequestPatternBuilder request = WireMock.postRequestedFor(WireMock.urlEqualTo(operation.path()));
        if (operation == AttributeOperation.RANDOM) {
            request.withRequestBody(WireMock.matchingJsonPath("$.keyUsages[0]", WireMock.equalTo("sign")));
        } else {
            request
                    .withRequestBody(
                            WireMock.matchingJsonPath("$.keyMeta[0].name", WireMock.equalTo("provider handle")));
        }
        mockServer.verify(request);
    }

    private void verifyIdentifierRequest(String path, String collectionName) {
        mockServer
                .verify(WireMock
                        .postRequestedFor(WireMock.urlEqualTo(path))
                        .withRequestBody(WireMock
                                .matchingJsonPath("$.%s[0].identifier".formatted(collectionName),
                                        WireMock.equalTo(ITEM_IDENTIFIER))));
    }

    private void verifySignRequest(OperationExecutionMode mode) {
        mockServer
                .verify(WireMock
                        .postRequestedFor(WireMock.urlEqualTo(SIGN_PATH))
                        .withRequestBody(WireMock.matchingJsonPath("$.executionMode", WireMock.equalTo(mode.getCode())))
                        .withRequestBody(
                                WireMock.matchingJsonPath("$.data[0].identifier", WireMock.equalTo(ITEM_IDENTIFIER))));
    }

    private void verifyOperationMetadataRequest(String path) {
        boolean ignoreArrayOrder = false;
        boolean ignoreExtraElements = false;
        mockServer
                .verify(WireMock
                        .postRequestedFor(WireMock.urlEqualTo(path))
                        .withRequestBody(WireMock
                                .equalToJson(VALID_TRACKING_REQUEST_JSON, ignoreArrayOrder, ignoreExtraElements)));
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

    private void assertValidationFailure(Executable call) {
        ConnectorException exception = assertThrows(ConnectorException.class, call);
        assertInstanceOf(IllegalArgumentException.class, exception.getCause(),
                () -> "expected a response-validation failure, got " + exception);
        assertSame(connector, exception.getConnector());
    }

    private static KeyScopedRequestV2Dto keyScopedRequest() {
        return withValidKeyScope(new KeyScopedRequestV2Dto());
    }

    private static TokenProfileScopedRequestV2Dto tokenProfileScopedRequest() {
        return withValidTokenProfileScope(new TokenProfileScopedRequestV2Dto());
    }

    private static CipherDataRequestV2Dto cipherRequest() {
        CipherDataRequestV2Dto request = withValidKeyScope(new CipherDataRequestV2Dto());
        request.setCipherAttributes(List.of());
        request.setCipherData(List.of(new CipherDataV2Dto(ITEM_DATA, ITEM_IDENTIFIER)));
        return request;
    }

    private static SignDataRequestV2Dto signRequest(OperationExecutionMode mode) {
        SignDataRequestV2Dto request = withValidKeyScope(new SignDataRequestV2Dto());
        request.setExecutionMode(mode);
        request.setSignatureAttributes(List.of());
        request.setData(List.of(new SignatureDataV2Dto(ITEM_DATA, ITEM_IDENTIFIER)));
        return request;
    }

    private static OperationTrackingRequestV2Dto signOperationRequest() {
        OperationTrackingRequestV2Dto request = new OperationTrackingRequestV2Dto();
        request.setOperationMeta(validMetadata());
        return request;
    }

    private static VerifyDataRequestV2Dto verifyRequest() {
        VerifyDataRequestV2Dto request = withValidKeyScope(new VerifyDataRequestV2Dto());
        request.setSignatureAttributes(List.of());
        request.setData(List.of(new SignatureDataV2Dto(ITEM_DATA, ITEM_IDENTIFIER)));
        request.setSignatures(List.of(new SignatureDataV2Dto(ITEM_DATA, ITEM_IDENTIFIER)));
        return request;
    }

    private static RandomDataRequestV2Dto randomRequest() {
        RandomDataRequestV2Dto request = withValidTokenProfileScope(new RandomDataRequestV2Dto());
        request.setLength(ITEM_DATA.length);
        request.setOperationAttributes(List.of());
        return request;
    }

    private static <T extends KeyScopedRequestV2Dto> T withValidKeyScope(T request) {
        withValidTokenProfileScope(request);
        request.setKeyMeta(validMetadata());
        return request;
    }

    private enum AttributeOperation {
        ENCRYPT(ENCRYPT_PATH + "/attributes"),
        DECRYPT(DECRYPT_PATH + "/attributes"),
        SIGN(SIGN_PATH + "/attributes"),
        VERIFY(VERIFY_PATH + "/attributes"),
        RANDOM(RANDOM_PATH + "/attributes");

        private final String path;

        AttributeOperation(String path) {
            this.path = path;
        }

        String path() {
            return path;
        }
    }

}
