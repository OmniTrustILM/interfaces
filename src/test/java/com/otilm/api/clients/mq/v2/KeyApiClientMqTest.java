package com.otilm.api.clients.mq.v2;

import com.otilm.api.exception.ConnectorException;
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
import com.otilm.api.model.connector.cryptography.v2.key.ExportKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ExportKeyResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ExportableKeyTypeV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportKeyAttributesRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportKeyResultRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportableKeyTypeV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyCreationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyCreationStatusResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyDestructionStatusResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyHandleV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyOperationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyOperationStatusResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyResolutionOutcome;
import com.otilm.api.model.connector.cryptography.v2.key.KeyResolutionV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListKeysRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListKeysResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ResolveKeysRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ResolveKeysResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.SecretKeyDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.SecretKeyOperationStatusResponseV2Dto;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.testsupport.RecordingProxyClient;
import com.otilm.api.testsupport.RecordingProxyClient.Invocation;
import com.otilm.api.testsupport.ValidatorFixture;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.KEY_REFERENCE;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validEncryptedKeyMaterial;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validExportKeyRequest;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validExportKeyResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadata;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadataAttribute;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validSecretKeyDataResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.withValidTokenProfileScope;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.withValidTokenScope;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeyApiClientMqTest {

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
    private static final String LIST_PATH = BASE_PATH + "/list";
    private static final String RESOLVE_PATH = BASE_PATH + "/resolve";
    private static final String KEY_IMPORT_ID = "key-import-1";
    private static final String TRANSPORT_PASSPHRASE = "8FQmS3ZbW1xkP0vTqA9rYcE4uHnJ6dLiKgOw2sXeVm0";
    private static final String KEY_CREATION_ID = "key-creation-1";

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();

    private KeyApiClient client;
    private ConnectorDto connector;
    private RecordingProxyClient proxyClient;

    @BeforeEach
    void setUp() {
        proxyClient = new RecordingProxyClient();
        connector = new ConnectorDto();
        OperationResponseValidator responseValidator = new OperationResponseValidator(VALIDATORS.validator());
        client = new KeyApiClient(proxyClient, responseValidator);
    }

    @Test
    void listCreateKeyAttributes_delegatesPostAndReturnsAttributes() throws ConnectorException {
        // given
        CreateKeyAttributesRequestV2Dto request = createKeyAttributesRequest();
        BaseAttribute attribute = validMetadataAttribute();
        proxyClient.respondWith(new BaseAttribute[]{attribute});

        // when
        List<BaseAttribute> result = client.listCreateKeyAttributes(connector, request);

        // then
        assertEquals(List.of(attribute), result);
        assertPlainInvocation(CREATE_ATTRIBUTES_PATH, request, BaseAttribute[].class);
    }

    @Test
    void listCreateKeyAttributes_rejectsInvalidResponse() {
        // given
        proxyClient.respondWith(new BaseAttribute[]{null});

        // when
        Executable call = () -> client.listCreateKeyAttributes(connector, createKeyAttributesRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void createKey_returnsSynchronousResponse() throws ConnectorException {
        // given
        CreateKeyRequestV2Dto request = createKeyRequest(OperationExecutionMode.SYNCHRONOUS);
        SecretKeyDataResponseV2Dto body = validSecretKeyDataResponse();
        proxyClient.respondWithEntity(ResponseEntity.ok(body));

        // when
        ResponseEntity<KeyCreationResponseV2Dto> result = client.createKey(connector, request);

        // then
        assertSame(body, result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEntityInvocation(BASE_PATH, request, KeyCreationResponseV2Dto.class);
    }

    @Test
    void createKey_returnsAsynchronousResponse() throws ConnectorException {
        // given
        CreateKeyRequestV2Dto request = createKeyRequest(OperationExecutionMode.ASYNCHRONOUS);
        SecretKeyDataResponseV2Dto body = new SecretKeyDataResponseV2Dto();
        body.setOperationMeta(validMetadata());
        proxyClient.respondWithEntity(ResponseEntity.accepted().body(body));

        // when
        ResponseEntity<KeyCreationResponseV2Dto> result = client.createKey(connector, request);

        // then
        assertSame(body, result.getBody());
        assertEquals(HttpStatus.ACCEPTED, result.getStatusCode());
        assertEntityInvocation(BASE_PATH, request, KeyCreationResponseV2Dto.class);
    }

    @Test
    void createKey_rejectsInvalidResponse() {
        // given
        SecretKeyDataResponseV2Dto responseWithoutKeyData = new SecretKeyDataResponseV2Dto();
        proxyClient.respondWithEntity(ResponseEntity.ok(responseWithoutKeyData));

        // when
        Executable call = () -> client.createKey(connector, createKeyRequest(OperationExecutionMode.SYNCHRONOUS));

        // then
        assertValidationFailure(call);
    }

    @Test
    void createKey_rejectsMismatchedKeyRequestType() {
        // given
        CreateKeyRequestV2Dto keyPairRequest = createKeyRequest(KeyRequestType.KEY_PAIR,
                OperationExecutionMode.SYNCHRONOUS);
        proxyClient.respondWithEntity(ResponseEntity.ok(validSecretKeyDataResponse()));

        // when
        Executable call = () -> client.createKey(connector, keyPairRequest);

        // then
        assertValidationFailure(call);
    }

    @Test
    void getCreateKeyStatus_delegatesPostAndReturnsStatus() throws ConnectorException {
        // given
        OperationTrackingRequestV2Dto request = keyOperationRequest();
        SecretKeyOperationStatusResponseV2Dto response = new SecretKeyOperationStatusResponseV2Dto();
        response.setStatus(OperationStatus.IN_PROGRESS);
        proxyClient.respondWith(response);

        // when
        KeyCreationStatusResponseV2Dto result = client.getCreateKeyStatus(connector, request);

        // then
        assertSame(response, result);
        assertPlainInvocation(CREATE_STATUS_PATH, request, KeyCreationStatusResponseV2Dto.class);
    }

    @Test
    void getCreateKeyStatus_rejectsInvalidResponse() {
        // given
        proxyClient.respondWith(new SecretKeyOperationStatusResponseV2Dto());

        // when
        Executable call = () -> client.getCreateKeyStatus(connector, keyOperationRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void cancelCreateKey_delegatesPostAndPreservesStatus() throws ConnectorException {
        // given
        OperationTrackingRequestV2Dto request = keyOperationRequest();
        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        proxyClient.respondWithEntity(response);

        // when
        ResponseEntity<Void> result = client.cancelCreateKey(connector, request);

        // then
        assertSame(response, result);
        assertEntityInvocation(CREATE_CANCEL_PATH, request, Void.class);
    }

    @Test
    void destroyKey_returnsSynchronousResponse() throws ConnectorException {
        // given
        DestroyKeyRequestV2Dto request = destroyKeyRequest(OperationExecutionMode.SYNCHRONOUS);
        KeyOperationResponseV2Dto body = new KeyOperationResponseV2Dto();
        ResponseEntity<KeyOperationResponseV2Dto> response = ResponseEntity.ok(body);
        proxyClient.respondWithEntity(response);

        // when
        ResponseEntity<KeyOperationResponseV2Dto> result = client.destroyKey(connector, request);

        // then
        assertSame(response, result);
        assertEntityInvocation(DESTROY_PATH, request, KeyOperationResponseV2Dto.class);
    }

    @Test
    void destroyKey_returnsAsynchronousResponse() throws ConnectorException {
        // given
        DestroyKeyRequestV2Dto request = destroyKeyRequest(OperationExecutionMode.ASYNCHRONOUS);
        KeyOperationResponseV2Dto body = new KeyOperationResponseV2Dto();
        body.setOperationMeta(validMetadata());
        ResponseEntity<KeyOperationResponseV2Dto> response = ResponseEntity.accepted().body(body);
        proxyClient.respondWithEntity(response);

        // when
        ResponseEntity<KeyOperationResponseV2Dto> result = client.destroyKey(connector, request);

        // then
        assertSame(response, result);
        assertEntityInvocation(DESTROY_PATH, request, KeyOperationResponseV2Dto.class);
    }

    @Test
    void destroyKey_rejectsInvalidResponse() {
        // given
        KeyOperationResponseV2Dto responseWithoutOperationMetadata = new KeyOperationResponseV2Dto();
        proxyClient.respondWithEntity(ResponseEntity.accepted().body(responseWithoutOperationMetadata));

        // when
        Executable call = () -> client.destroyKey(connector, destroyKeyRequest(OperationExecutionMode.ASYNCHRONOUS));

        // then
        assertValidationFailure(call);
    }

    @Test
    void getDestroyKeyStatus_delegatesPostAndReturnsStatus() throws ConnectorException {
        // given
        OperationTrackingRequestV2Dto request = keyOperationRequest();
        KeyDestructionStatusResponseV2Dto response = new KeyDestructionStatusResponseV2Dto();
        response.setStatus(OperationStatus.IN_PROGRESS);
        proxyClient.respondWith(response);

        // when
        KeyOperationStatusResponseV2Dto result = client.getDestroyKeyStatus(connector, request);

        // then
        assertSame(response, result);
        assertPlainInvocation(DESTROY_STATUS_PATH, request, KeyDestructionStatusResponseV2Dto.class);
    }

    @Test
    void getDestroyKeyStatus_rejectsInvalidResponse() {
        // given
        proxyClient.respondWith(new KeyDestructionStatusResponseV2Dto());

        // when
        Executable call = () -> client.getDestroyKeyStatus(connector, keyOperationRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void cancelDestroyKey_delegatesPostAndPreservesStatus() throws ConnectorException {
        // given
        OperationTrackingRequestV2Dto request = keyOperationRequest();
        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        proxyClient.respondWithEntity(response);

        // when
        ResponseEntity<Void> result = client.cancelDestroyKey(connector, request);

        // then
        assertSame(response, result);
        assertEntityInvocation(DESTROY_CANCEL_PATH, request, Void.class);
    }

    @Test
    void createKey_wrapsProxyRuntimeFailure() {
        // given
        RuntimeException proxyFailure = new IllegalStateException("proxy failure");
        proxyClient.failWith(proxyFailure);

        // when
        Executable call = () -> client.createKey(connector, createKeyRequest(OperationExecutionMode.SYNCHRONOUS));

        // then
        ConnectorException exception = assertThrows(ConnectorException.class, call);
        assertSame(proxyFailure, exception.getCause());
        assertSame(connector, exception.getConnector());
    }

    @Test
    void listImportableKeyTypes_delegatesPostAndReturnsTypes() throws ConnectorException {
        // given
        TokenProfileScopedRequestV2Dto request = withValidTokenProfileScope(new TokenProfileScopedRequestV2Dto());
        ImportableKeyTypeV2Dto importableKeyType = importableKeyType();
        proxyClient.respondWith(new ImportableKeyTypeV2Dto[]{importableKeyType});

        // when
        List<ImportableKeyTypeV2Dto> result = client.listImportableKeyTypes(connector, request);

        // then
        assertEquals(List.of(importableKeyType), result);
        assertPlainInvocation(IMPORT_KEY_TYPES_PATH, request, ImportableKeyTypeV2Dto[].class);
    }

    @Test
    void listImportKeyAttributes_delegatesPostAndReturnsAttributes() throws ConnectorException {
        // given
        ImportKeyAttributesRequestV2Dto request = importKeyAttributesRequest();
        BaseAttribute attribute = validMetadataAttribute();
        proxyClient.respondWith(new BaseAttribute[]{attribute});

        // when
        List<BaseAttribute> result = client.listImportKeyAttributes(connector, request);

        // then
        assertEquals(List.of(attribute), result);
        assertPlainInvocation(IMPORT_ATTRIBUTES_PATH, request, BaseAttribute[].class);
    }

    @Test
    void importKey_returnsSynchronousResponse() throws ConnectorException {
        // given
        ImportKeyRequestV2Dto request = importKeyRequest(OperationExecutionMode.SYNCHRONOUS);
        SecretKeyDataResponseV2Dto body = validSecretKeyDataResponse();
        proxyClient.respondWithEntity(ResponseEntity.ok(body));

        // when
        ResponseEntity<KeyCreationResponseV2Dto> result = client.importKey(connector, request);

        // then
        assertSame(body, result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEntityInvocation(IMPORT_PATH, request, KeyCreationResponseV2Dto.class);
    }

    @Test
    void importKey_rejectsResponseForAnotherKeyType() {
        // given
        ImportKeyRequestV2Dto request = importKeyRequest(OperationExecutionMode.SYNCHRONOUS);
        request.setKeyRequestType(KeyRequestType.KEY_PAIR);
        proxyClient.respondWithEntity(ResponseEntity.ok(validSecretKeyDataResponse()));

        // when
        // then
        assertValidationFailure(() -> client.importKey(connector, request));
    }

    @Test
    void getImportKeyStatus_delegatesPostAndReturnsStatus() throws ConnectorException {
        // given
        OperationTrackingRequestV2Dto request = keyOperationRequest();
        SecretKeyOperationStatusResponseV2Dto status = completedImportStatus();
        proxyClient.respondWith(status);

        // when
        KeyCreationStatusResponseV2Dto result = client.getImportKeyStatus(connector, request);

        // then
        assertSame(status, result);
        assertPlainInvocation(IMPORT_STATUS_PATH, request, KeyCreationStatusResponseV2Dto.class);
    }

    @Test
    void cancelImportKey_delegatesPostAndPreservesStatus() throws ConnectorException {
        // given
        OperationTrackingRequestV2Dto request = keyOperationRequest();
        proxyClient.respondWithEntity(ResponseEntity.noContent().build());

        // when
        ResponseEntity<Void> result = client.cancelImportKey(connector, request);

        // then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertEntityInvocation(IMPORT_CANCEL_PATH, request, Void.class);
    }

    @Test
    void getImportKeyResult_delegatesPostAndReturnsRecordedOutcome() throws ConnectorException {
        // given
        ImportKeyResultRequestV2Dto request = importKeyResultRequest();
        SecretKeyOperationStatusResponseV2Dto status = completedImportStatus();
        proxyClient.respondWith(status);

        // when
        KeyCreationStatusResponseV2Dto result = client.getImportKeyResult(connector, request);

        // then
        assertSame(status, result);
        assertPlainInvocation(IMPORT_RESULT_PATH, request, KeyCreationStatusResponseV2Dto.class);
    }

    @Test
    void listExportableKeyTypes_delegatesPostAndReturnsTypes() throws ConnectorException {
        // given
        TokenProfileScopedRequestV2Dto request = withValidTokenProfileScope(new TokenProfileScopedRequestV2Dto());
        ExportableKeyTypeV2Dto exportableKeyType = exportableKeyType();
        proxyClient.respondWith(new ExportableKeyTypeV2Dto[]{exportableKeyType});

        // when
        List<ExportableKeyTypeV2Dto> result = client.listExportableKeyTypes(connector, request);

        // then
        assertEquals(List.of(exportableKeyType), result);
        assertPlainInvocation(EXPORT_KEY_TYPES_PATH, request, ExportableKeyTypeV2Dto[].class);
    }

    @Test
    void listExportKeyAttributes_delegatesPostAndReturnsAttributes() throws ConnectorException {
        // given
        KeyScopedRequestV2Dto request = withValidTokenProfileScope(new KeyScopedRequestV2Dto());
        request.setKeyMeta(validMetadata());
        BaseAttribute attribute = validMetadataAttribute();
        proxyClient.respondWith(new BaseAttribute[]{attribute});

        // when
        List<BaseAttribute> result = client.listExportKeyAttributes(connector, request);

        // then
        assertEquals(List.of(attribute), result);
        assertPlainInvocation(EXPORT_ATTRIBUTES_PATH, request, BaseAttribute[].class);
    }

    @Test
    void listKeys_delegatesPostAndReturnsThePage() throws ConnectorException {
        // given
        ListKeysRequestV2Dto request = withValidTokenProfileScope(new ListKeysRequestV2Dto());
        request.setKeyRequestType(KeyRequestType.KEY_PAIR);
        request.setOffset(0);
        request.setLimit(10);
        ListKeysResponseV2Dto page = new ListKeysResponseV2Dto();
        page.setItems(List.of());
        page.setTotalItems(0L);
        proxyClient.respondWith(page);

        // when
        ListKeysResponseV2Dto result = client.listKeys(connector, request);

        // then
        assertSame(page, result);
        assertPlainInvocation(LIST_PATH, request, ListKeysResponseV2Dto.class);
    }

    @Test
    void listKeys_rejectsAPageWithoutItems() {
        // given
        ListKeysRequestV2Dto request = withValidTokenProfileScope(new ListKeysRequestV2Dto());
        request.setKeyRequestType(KeyRequestType.KEY_PAIR);
        request.setOffset(0);
        request.setLimit(10);
        ListKeysResponseV2Dto withoutItems = new ListKeysResponseV2Dto();
        withoutItems.setTotalItems(0L);
        proxyClient.respondWith(withoutItems);

        // when
        Executable call = () -> client.listKeys(connector, request);

        // then
        ConnectorException exception = assertThrows(ConnectorException.class, call);
        assertEquals("Connector response validation failed: items: items is required",
                exception.getCause().getMessage());
    }

    @Test
    void resolveKeys_delegatesPostAndReturnsTheResults() throws ConnectorException {
        // given
        ResolveKeysRequestV2Dto request = resolveKeysRequest();
        ResolveKeysResponseV2Dto response = new ResolveKeysResponseV2Dto(
                List.of(new KeyResolutionV2Dto("0", KeyResolutionOutcome.NOT_FOUND, null)));
        proxyClient.respondWith(response);

        // when
        ResolveKeysResponseV2Dto result = client.resolveKeys(connector, request);

        // then
        assertSame(response, result);
        assertPlainInvocation(RESOLVE_PATH, request, ResolveKeysResponseV2Dto.class);
    }

    @Test
    void resolveKeys_rejectsAResponseWithoutResults() {
        // given
        proxyClient.respondWith(new ResolveKeysResponseV2Dto());

        // when
        Executable call = () -> client.resolveKeys(connector, resolveKeysRequest());

        // then
        ConnectorException exception = assertThrows(ConnectorException.class, call);
        assertEquals("Connector response validation failed: results: results is required",
                exception.getCause().getMessage());
    }

    private static ResolveKeysRequestV2Dto resolveKeysRequest() {
        return ResolveKeysRequestV2Dto
                .builder()
                .scope(withValidTokenProfileScope(new TokenProfileScopedRequestV2Dto()))
                .keyRequestType(KeyRequestType.KEY_PAIR)
                .keys(List.of(new KeyHandleV2Dto("0", validMetadata())))
                .build();
    }

    @Test
    void exportKey_delegatesPostAndReturnsProtectedMaterial() throws ConnectorException {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();
        ExportKeyResponseV2Dto response = validExportKeyResponse();
        proxyClient.respondWithEntity(ResponseEntity.ok(response));

        // when
        ExportKeyResponseV2Dto result = client.exportKey(connector, request);

        // then
        assertSame(response, result);
        assertEntityInvocation(EXPORT_PATH, request, ExportKeyResponseV2Dto.class);
    }

    @Test
    void exportKey_rejectsResponseThatEchoesAnotherKeyReference() {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();
        ExportKeyResponseV2Dto response = validExportKeyResponse();
        response.setKeyReference("2f3d4e5a-6b7c-48d9-90e1-a2b3c4d5e6f7");
        proxyClient.respondWithEntity(ResponseEntity.ok(response));

        // when
        // then
        assertValidationFailure(() -> client.exportKey(connector, request));
    }

    @Test
    void exportKey_rejectsAnAsynchronousResponse() {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();
        proxyClient.respondWithEntity(ResponseEntity.accepted().body(validExportKeyResponse()));

        // when
        // then
        assertValidationFailure(() -> client.exportKey(connector, request));
    }

    private static ExportableKeyTypeV2Dto exportableKeyType() {
        ExportableKeyTypeV2Dto exportableKeyType = new ExportableKeyTypeV2Dto();
        exportableKeyType.setKeyRequestType(KeyRequestType.KEY_PAIR);
        exportableKeyType.setAlgorithms(Set.of(KeyAlgorithm.RSA));
        return exportableKeyType;
    }

    private static ImportKeyRequestV2Dto importKeyRequest(OperationExecutionMode executionMode) {
        ImportKeyRequestV2Dto request = withValidTokenProfileScope(new ImportKeyRequestV2Dto());
        request.setKeyImportId(KEY_IMPORT_ID);
        request.setKeyReference(KEY_REFERENCE);
        request.setExecutionMode(executionMode);
        request.setKeyRequestType(KeyRequestType.SECRET);
        request.setImportKeyAttributes(List.of());
        request.setMaterial(validEncryptedKeyMaterial());
        request.setPassphrase(TRANSPORT_PASSPHRASE);
        request.setExportable(Boolean.FALSE);
        return request;
    }

    private static ImportKeyAttributesRequestV2Dto importKeyAttributesRequest() {
        ImportKeyAttributesRequestV2Dto request = withValidTokenProfileScope(new ImportKeyAttributesRequestV2Dto());
        request.setKeyRequestType(KeyRequestType.SECRET);
        return request;
    }

    private static ImportKeyResultRequestV2Dto importKeyResultRequest() {
        ImportKeyResultRequestV2Dto request = withValidTokenScope(new ImportKeyResultRequestV2Dto());
        request.setKeyImportId(KEY_IMPORT_ID);
        return request;
    }

    private static ImportableKeyTypeV2Dto importableKeyType() {
        ImportableKeyTypeV2Dto importableKeyType = new ImportableKeyTypeV2Dto();
        importableKeyType.setKeyRequestType(KeyRequestType.KEY_PAIR);
        importableKeyType.setAlgorithms(Set.of(KeyAlgorithm.RSA));
        return importableKeyType;
    }

    private static SecretKeyOperationStatusResponseV2Dto completedImportStatus() {
        SecretKeyOperationStatusResponseV2Dto status = new SecretKeyOperationStatusResponseV2Dto();
        status.setStatus(OperationStatus.COMPLETED);
        status.setResult(validSecretKeyDataResponse());
        return status;
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

    private static OperationTrackingRequestV2Dto keyOperationRequest() {
        OperationTrackingRequestV2Dto request = new OperationTrackingRequestV2Dto();
        request.setOperationMeta(validMetadata());
        return request;
    }
}
