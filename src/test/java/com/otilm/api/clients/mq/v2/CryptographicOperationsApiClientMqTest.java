package com.otilm.api.clients.mq.v2;

import com.otilm.api.exception.ConnectorException;
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
import com.otilm.api.model.connector.cryptography.v2.operations.data.SignatureResultItemV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.data.VerificationResponseItemV2Dto;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.testsupport.RecordingProxyClient;
import com.otilm.api.testsupport.RecordingProxyClient.Invocation;
import com.otilm.api.testsupport.ValidatorFixture;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.ResponseEntity;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadata;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadataAttribute;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.withValidTokenProfileScope;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

class CryptographicOperationsApiClientMqTest {

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

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();

    private CryptographicOperationsApiClient client;
    private ConnectorDto connector;
    private RecordingProxyClient proxyClient;

    @BeforeEach
    void setUp() {
        proxyClient = new RecordingProxyClient();
        connector = new ConnectorDto();
        OperationResponseValidator responseValidator = new OperationResponseValidator(VALIDATORS.validator());
        client = new CryptographicOperationsApiClient(proxyClient, responseValidator);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("attributeOperations")
    void attributeOperation_delegatesPostAndReturnsAttributes(AttributeOperation operation) throws ConnectorException {
        // given
        Object request = attributeRequest(operation);
        BaseAttribute attribute = validMetadataAttribute();
        proxyClient.respondWith(new BaseAttribute[]{attribute});

        // when
        List<BaseAttribute> result = invokeAttributeOperation(operation, request);

        // then
        assertEquals(List.of(attribute), result);
        assertPlainInvocation(operation.path(), request, BaseAttribute[].class);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("attributeOperations")
    void attributeOperation_rejectsInvalidResponse(AttributeOperation operation) {
        // given
        Object request = attributeRequest(operation);
        proxyClient.respondWith(new BaseAttribute[]{null});

        // when
        Executable call = () -> invokeAttributeOperation(operation, request);

        // then
        assertValidationFailure(call);
    }

    @Test
    void encryptData_delegatesPostAndReturnsEncryptedData() throws ConnectorException {
        // given
        CipherDataRequestV2Dto request = cipherRequest();
        EncryptDataResponseV2Dto response = new EncryptDataResponseV2Dto();
        response.setEncryptedData(List.of(new CipherDataV2Dto(ITEM_DATA, ITEM_IDENTIFIER)));
        proxyClient.respondWith(response);

        // when
        EncryptDataResponseV2Dto result = client.encryptData(connector, request);

        // then
        assertSame(response, result);
        assertPlainInvocation(ENCRYPT_PATH, request, EncryptDataResponseV2Dto.class);
    }

    @Test
    void encryptData_rejectsInvalidResponse() {
        // given
        proxyClient.respondWith(new EncryptDataResponseV2Dto());

        // when
        Executable call = () -> client.encryptData(connector, cipherRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void encryptData_rejectsResponseWithDifferentIdentifier() {
        // given
        EncryptDataResponseV2Dto response = new EncryptDataResponseV2Dto();
        response.setEncryptedData(List.of(new CipherDataV2Dto(ITEM_DATA, DIFFERENT_ITEM_IDENTIFIER)));
        proxyClient.respondWith(response);

        // when
        Executable call = () -> client.encryptData(connector, cipherRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void decryptData_delegatesPostAndReturnsDecryptedData() throws ConnectorException {
        // given
        CipherDataRequestV2Dto request = cipherRequest();
        DecryptDataResponseV2Dto response = new DecryptDataResponseV2Dto();
        response.setDecryptedData(List.of(new CipherDataV2Dto(ITEM_DATA, ITEM_IDENTIFIER)));
        proxyClient.respondWith(response);

        // when
        DecryptDataResponseV2Dto result = client.decryptData(connector, request);

        // then
        assertSame(response, result);
        assertPlainInvocation(DECRYPT_PATH, request, DecryptDataResponseV2Dto.class);
    }

    @Test
    void decryptData_rejectsInvalidResponse() {
        // given
        proxyClient.respondWith(new DecryptDataResponseV2Dto());

        // when
        Executable call = () -> client.decryptData(connector, cipherRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void decryptData_rejectsResponseWithDifferentIdentifier() {
        // given
        DecryptDataResponseV2Dto response = new DecryptDataResponseV2Dto();
        response.setDecryptedData(List.of(new CipherDataV2Dto(ITEM_DATA, DIFFERENT_ITEM_IDENTIFIER)));
        proxyClient.respondWith(response);

        // when
        Executable call = () -> client.decryptData(connector, cipherRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void signData_returnsSynchronousResponse() throws ConnectorException {
        // given
        SignDataRequestV2Dto request = signRequest(OperationExecutionMode.SYNCHRONOUS);
        SignDataResponseV2Dto body = new SignDataResponseV2Dto();
        body.setSignatures(List.of(new SignatureDataV2Dto(ITEM_DATA, ITEM_IDENTIFIER)));
        ResponseEntity<SignDataResponseV2Dto> response = ResponseEntity.ok(body);
        proxyClient.respondWithEntity(response);

        // when
        ResponseEntity<SignDataResponseV2Dto> result = client.signData(connector, request);

        // then
        assertSame(response, result);
        assertEntityInvocation(SIGN_PATH, request, SignDataResponseV2Dto.class);
    }

    @Test
    void signData_returnsAsynchronousResponse() throws ConnectorException {
        // given
        SignDataRequestV2Dto request = signRequest(OperationExecutionMode.ASYNCHRONOUS);
        SignDataResponseV2Dto body = new SignDataResponseV2Dto();
        body.setOperationMeta(validMetadata());
        ResponseEntity<SignDataResponseV2Dto> response = ResponseEntity.accepted().body(body);
        proxyClient.respondWithEntity(response);

        // when
        ResponseEntity<SignDataResponseV2Dto> result = client.signData(connector, request);

        // then
        assertSame(response, result);
        assertEntityInvocation(SIGN_PATH, request, SignDataResponseV2Dto.class);
    }

    @Test
    void signData_rejectsInvalidResponse() {
        // given
        proxyClient.respondWithEntity(ResponseEntity.ok(new SignDataResponseV2Dto()));

        // when
        Executable call = () -> client.signData(connector, signRequest(OperationExecutionMode.SYNCHRONOUS));

        // then
        assertValidationFailure(call);
    }

    @Test
    void getSignStatus_delegatesPostAndReturnsStatus() throws ConnectorException {
        // given
        OperationTrackingRequestV2Dto request = signOperationRequest();
        SignOperationStatusResponseV2Dto response = new SignOperationStatusResponseV2Dto();
        response
                .setItems(List
                        .of(new SignatureResultItemV2Dto(ITEM_IDENTIFIER, OperationStatus.COMPLETED, ITEM_DATA, null)));
        proxyClient.respondWith(response);

        // when
        SignOperationStatusResponseV2Dto result = client.getSignStatus(connector, request);

        // then
        assertSame(response, result);
        assertPlainInvocation(SIGN_STATUS_PATH, request, SignOperationStatusResponseV2Dto.class);
    }

    @Test
    void getSignStatus_rejectsInvalidResponse() {
        // given
        proxyClient.respondWith(new SignOperationStatusResponseV2Dto());

        // when
        Executable call = () -> client.getSignStatus(connector, signOperationRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void cancelSign_delegatesPostAndPreservesStatus() throws ConnectorException {
        // given
        OperationTrackingRequestV2Dto request = signOperationRequest();
        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        proxyClient.respondWithEntity(response);

        // when
        ResponseEntity<Void> result = client.cancelSign(connector, request);

        // then
        assertSame(response, result);
        assertEntityInvocation(SIGN_CANCEL_PATH, request, Void.class);
    }

    @Test
    void verifyData_delegatesPostAndReturnsVerifications() throws ConnectorException {
        // given
        VerifyDataRequestV2Dto request = verifyRequest();
        VerifyDataResponseV2Dto response = new VerifyDataResponseV2Dto();
        response.setVerifications(List.of(new VerificationResponseItemV2Dto(true, ITEM_IDENTIFIER, null)));
        proxyClient.respondWith(response);

        // when
        VerifyDataResponseV2Dto result = client.verifyData(connector, request);

        // then
        assertSame(response, result);
        assertPlainInvocation(VERIFY_PATH, request, VerifyDataResponseV2Dto.class);
    }

    @Test
    void verifyData_rejectsInvalidResponse() {
        // given
        proxyClient.respondWith(new VerifyDataResponseV2Dto());

        // when
        Executable call = () -> client.verifyData(connector, verifyRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void verifyData_rejectsResponseWithDifferentIdentifier() {
        // given
        VerifyDataResponseV2Dto response = new VerifyDataResponseV2Dto();
        response.setVerifications(List.of(new VerificationResponseItemV2Dto(true, DIFFERENT_ITEM_IDENTIFIER, null)));
        proxyClient.respondWith(response);

        // when
        Executable call = () -> client.verifyData(connector, verifyRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void randomData_delegatesPostAndReturnsRandomData() throws ConnectorException {
        // given
        RandomDataRequestV2Dto request = randomRequest();
        RandomDataResponseV2Dto response = new RandomDataResponseV2Dto();
        response.setData(ITEM_DATA);
        proxyClient.respondWith(response);

        // when
        RandomDataResponseV2Dto result = client.randomData(connector, request);

        // then
        assertSame(response, result);
        assertArrayEquals(ITEM_DATA, result.getData());
        assertPlainInvocation(RANDOM_PATH, request, RandomDataResponseV2Dto.class);
    }

    @Test
    void randomData_rejectsInvalidResponse() {
        // given
        proxyClient.respondWith(new RandomDataResponseV2Dto());

        // when
        Executable call = () -> client.randomData(connector, randomRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void randomData_rejectsResponseWithDifferentLength() {
        // given
        byte[] twoByteResponse = {1, 2};
        RandomDataResponseV2Dto response = new RandomDataResponseV2Dto();
        response.setData(twoByteResponse);
        proxyClient.respondWith(response);

        // when
        Executable call = () -> client.randomData(connector, randomRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void encryptData_wrapsProxyRuntimeFailure() {
        // given
        RuntimeException proxyFailure = new IllegalStateException("proxy failure");
        proxyClient.failWith(proxyFailure);

        // when
        Executable call = () -> client.encryptData(connector, cipherRequest());

        // then
        ConnectorException exception = assertThrows(ConnectorException.class, call);
        assertSame(proxyFailure, exception.getCause());
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

    private List<BaseAttribute> invokeAttributeOperation(AttributeOperation operation, Object request)
            throws ConnectorException {
        return switch (operation) {
            case ENCRYPT -> client.listEncryptAttributes(connector, (KeyScopedRequestV2Dto) request);
            case DECRYPT -> client.listDecryptAttributes(connector, (KeyScopedRequestV2Dto) request);
            case SIGN -> client.listSignAttributes(connector, (KeyScopedRequestV2Dto) request);
            case VERIFY -> client.listVerifyAttributes(connector, (KeyScopedRequestV2Dto) request);
            case RANDOM -> client.listRandomAttributes(connector, (TokenProfileScopedRequestV2Dto) request);
        };
    }

    private static Object attributeRequest(AttributeOperation operation) {
        return operation == AttributeOperation.RANDOM ? tokenProfileScopedRequest() : keyScopedRequest();
    }

    private void assertPlainInvocation(String path, Object body, Class<?> responseType) {
        assertInvocation(path, body, responseType, false);
    }

    private void assertEntityInvocation(String path, Object body, Class<?> responseType) {
        assertInvocation(path, body, responseType, true);
    }

    private void assertInvocation(String path, Object body, Class<?> responseType, boolean entityResponse) {
        Invocation invocation = proxyClient.invocation();
        assertSame(connector, invocation.connector());
        assertEquals(path, invocation.path());
        assertEquals("POST", invocation.method());
        assertSame(body, invocation.body());
        assertEquals(responseType, invocation.responseType());
        if (entityResponse) {
            assertTrue(invocation.entityResponse());
        } else {
            assertFalse(invocation.entityResponse());
        }
    }

    private void assertValidationFailure(Executable call) {
        ConnectorException exception = assertThrows(ConnectorException.class, call);
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        assertSame(connector, exception.getConnector());
    }

    private static KeyScopedRequestV2Dto keyScopedRequest() {
        KeyScopedRequestV2Dto request = withValidTokenProfileScope(new KeyScopedRequestV2Dto());
        request.setKeyMeta(validMetadata());
        return request;
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
