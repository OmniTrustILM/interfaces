package com.otilm.api.clients.mq.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.mq.ProxyClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.interfaces.client.v2.KeySyncApiClient;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.OperationResponseValidator;
import com.otilm.api.model.connector.cryptography.v2.OperationTrackingRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.OperationValidationResult;
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
import com.otilm.api.model.connector.cryptography.v2.key.KeyOperationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListKeysRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListKeysResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ResolveKeysRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ResolveKeysResponseV2Dto;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.ResponseEntity;

/**
 * MQ implementation of the connector-facing stateless cryptography v2 key API.
 *
 * <p>
 * Creation, destruction, import and export read the upstream status to tell a synchronous completion from an
 * asynchronous acceptance, and export refuses anything but HTTP 200. They therefore require a {@link ProxyClient} that
 * overrides {@link ProxyClient#sendRequestForEntity} and propagates the real status: the default implementation reports
 * every successful response as HTTP 200, which would let a status this contract does not describe pass as a synchronous
 * success.
 * </p>
 */
@SuppressWarnings("java:S1075")
public class KeyApiClient implements KeySyncApiClient {

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
    private static final String POST = "POST";
    private final ProxyClient proxyClient;
    private final OperationResponseValidator responseValidator;

    public KeyApiClient(ProxyClient proxyClient, OperationResponseValidator responseValidator) {
        this.proxyClient = proxyClient;
        this.responseValidator = responseValidator;
    }

    private static void requireValid(OperationValidationResult validation, ApiClientConnectorInfo connector)
            throws ConnectorException {
        if (!validation.isValid()) {
            IllegalArgumentException cause = validation.getCause();
            throw new ConnectorException(cause.getMessage(), cause, connector);
        }
    }

    @Override
    public List<BaseAttribute> listCreateKeyAttributes(ApiClientConnectorInfo connector,
            CreateKeyAttributesRequestV2Dto request) throws ConnectorException {
        List<BaseAttribute> attributes = sendForList(connector, CREATE_ATTRIBUTES_PATH, request, BaseAttribute[].class);
        requireValid(responseValidator.validateAttributeList(attributes), connector);
        return attributes;
    }

    @Override
    public ResponseEntity<KeyCreationResponseV2Dto> createKey(ApiClientConnectorInfo connector,
            CreateKeyRequestV2Dto request) throws ConnectorException {
        ResponseEntity<KeyCreationResponseV2Dto> response = sendEntity(connector, BASE_PATH, request,
                KeyCreationResponseV2Dto.class);
        requireValid(responseValidator.validateCreateKey(request, response), connector);
        return response;
    }

    @Override
    public KeyCreationStatusResponseV2Dto getCreateKeyStatus(ApiClientConnectorInfo connector,
            OperationTrackingRequestV2Dto request) throws ConnectorException {
        KeyCreationStatusResponseV2Dto response = send(connector, CREATE_STATUS_PATH, request,
                KeyCreationStatusResponseV2Dto.class);
        requireValid(responseValidator.validateCreateKeyStatus(response), connector);
        return response;
    }

    @Override
    public ResponseEntity<Void> cancelCreateKey(ApiClientConnectorInfo connector, OperationTrackingRequestV2Dto request)
            throws ConnectorException {
        return sendEntity(connector, CREATE_CANCEL_PATH, request, Void.class);
    }

    @Override
    public ResponseEntity<KeyOperationResponseV2Dto> destroyKey(ApiClientConnectorInfo connector,
            DestroyKeyRequestV2Dto request) throws ConnectorException {
        ResponseEntity<KeyOperationResponseV2Dto> response = sendEntity(connector, DESTROY_PATH, request,
                KeyOperationResponseV2Dto.class);
        requireValid(responseValidator.validateDestroy(request.getExecutionMode(), response), connector);
        return response;
    }

    @Override
    public KeyDestructionStatusResponseV2Dto getDestroyKeyStatus(ApiClientConnectorInfo connector,
            OperationTrackingRequestV2Dto request) throws ConnectorException {
        KeyDestructionStatusResponseV2Dto response = send(connector, DESTROY_STATUS_PATH, request,
                KeyDestructionStatusResponseV2Dto.class);
        requireValid(responseValidator.validateDestroyKeyStatus(response), connector);
        return response;
    }

    @Override
    public ResponseEntity<Void> cancelDestroyKey(ApiClientConnectorInfo connector,
            OperationTrackingRequestV2Dto request) throws ConnectorException {
        return sendEntity(connector, DESTROY_CANCEL_PATH, request, Void.class);
    }

    @Override
    public List<ImportableKeyTypeV2Dto> listImportableKeyTypes(ApiClientConnectorInfo connector,
            TokenProfileScopedRequestV2Dto request) throws ConnectorException {
        List<ImportableKeyTypeV2Dto> keyTypes = sendForList(connector, IMPORT_KEY_TYPES_PATH, request,
                ImportableKeyTypeV2Dto[].class);
        requireValid(responseValidator.keyTransfer().validateImportableKeyTypes(keyTypes), connector);
        return keyTypes;
    }

    @Override
    public List<BaseAttribute> listImportKeyAttributes(ApiClientConnectorInfo connector,
            ImportKeyAttributesRequestV2Dto request) throws ConnectorException {
        List<BaseAttribute> attributes = sendForList(connector, IMPORT_ATTRIBUTES_PATH, request, BaseAttribute[].class);
        requireValid(responseValidator.validateAttributeList(attributes), connector);
        return attributes;
    }

    @Override
    public ResponseEntity<KeyCreationResponseV2Dto> importKey(ApiClientConnectorInfo connector,
            ImportKeyRequestV2Dto request) throws ConnectorException {
        ResponseEntity<KeyCreationResponseV2Dto> response = sendEntity(connector, IMPORT_PATH, request,
                KeyCreationResponseV2Dto.class);
        requireValid(responseValidator.keyTransfer().validateImportKey(request, response), connector);
        return response;
    }

    @Override
    public KeyCreationStatusResponseV2Dto getImportKeyStatus(ApiClientConnectorInfo connector,
            OperationTrackingRequestV2Dto request) throws ConnectorException {
        return importOutcome(connector, IMPORT_STATUS_PATH, request);
    }

    @Override
    public ResponseEntity<Void> cancelImportKey(ApiClientConnectorInfo connector, OperationTrackingRequestV2Dto request)
            throws ConnectorException {
        return sendEntity(connector, IMPORT_CANCEL_PATH, request, Void.class);
    }

    @Override
    public KeyCreationStatusResponseV2Dto getImportKeyResult(ApiClientConnectorInfo connector,
            ImportKeyResultRequestV2Dto request) throws ConnectorException {
        return importOutcome(connector, IMPORT_RESULT_PATH, request);
    }

    @Override
    public List<ExportableKeyTypeV2Dto> listExportableKeyTypes(ApiClientConnectorInfo connector,
            TokenProfileScopedRequestV2Dto request) throws ConnectorException {
        List<ExportableKeyTypeV2Dto> keyTypes = sendForList(connector, EXPORT_KEY_TYPES_PATH, request,
                ExportableKeyTypeV2Dto[].class);
        requireValid(responseValidator.keyTransfer().validateExportableKeyTypes(keyTypes), connector);
        return keyTypes;
    }

    @Override
    public List<BaseAttribute> listExportKeyAttributes(ApiClientConnectorInfo connector, KeyScopedRequestV2Dto request)
            throws ConnectorException {
        List<BaseAttribute> attributes = sendForList(connector, EXPORT_ATTRIBUTES_PATH, request, BaseAttribute[].class);
        requireValid(responseValidator.validateAttributeList(attributes), connector);
        return attributes;
    }

    @Override
    public ExportKeyResponseV2Dto exportKey(ApiClientConnectorInfo connector, ExportKeyRequestV2Dto request)
            throws ConnectorException {
        ResponseEntity<ExportKeyResponseV2Dto> response = sendEntity(connector, EXPORT_PATH, request,
                ExportKeyResponseV2Dto.class);
        requireValid(responseValidator.keyTransfer().validateExportKey(request, response), connector);
        return response.getBody();
    }

    @Override
    public ListKeysResponseV2Dto listKeys(ApiClientConnectorInfo connector, ListKeysRequestV2Dto request)
            throws ConnectorException {
        ListKeysResponseV2Dto page = send(connector, LIST_PATH, request, ListKeysResponseV2Dto.class);
        requireValid(responseValidator.validateListKeys(request, page), connector);
        return page;
    }

    @Override
    public ResolveKeysResponseV2Dto resolveKeys(ApiClientConnectorInfo connector, ResolveKeysRequestV2Dto request)
            throws ConnectorException {
        ResolveKeysResponseV2Dto response = send(connector, RESOLVE_PATH, request, ResolveKeysResponseV2Dto.class);
        requireValid(responseValidator.validateResolveKeys(request, response), connector);
        return response;
    }

    private KeyCreationStatusResponseV2Dto importOutcome(ApiClientConnectorInfo connector, String path, Object request)
            throws ConnectorException {
        KeyCreationStatusResponseV2Dto response = send(connector, path, request, KeyCreationStatusResponseV2Dto.class);
        requireValid(responseValidator.keyTransfer().validateImportKeyStatus(response), connector);
        return response;
    }

    private <T> List<T> sendForList(ApiClientConnectorInfo connector, String path, Object body, Class<T[]> responseType)
            throws ConnectorException {
        T[] response = send(connector, path, body, responseType);
        if (response == null) {
            throw new ConnectorException("Connector returned an empty body for " + path, connector);
        }
        return Arrays.asList(response);
    }

    private <T> T send(ApiClientConnectorInfo connector, String path, Object body, Class<T> responseType)
            throws ConnectorException {
        try {
            return proxyClient.sendRequest(connector, path, POST, body, responseType);
        } catch (RuntimeException e) {
            throw new ConnectorException("Key request failed for " + path, e, connector);
        }
    }

    private <T> ResponseEntity<T> sendEntity(ApiClientConnectorInfo connector, String path, Object body,
            Class<T> responseType) throws ConnectorException {
        try {
            return proxyClient.sendRequestForEntity(connector, path, POST, body, responseType);
        } catch (RuntimeException e) {
            throw new ConnectorException("Key request failed for " + path, e, connector);
        }
    }
}
