package com.otilm.api.clients.cryptography.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.BaseApiClient;
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
import java.util.List;
import javax.net.ssl.TrustManager;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient implementation of the connector-facing stateless cryptography v2 key API.
 */
@SuppressWarnings("java:S1075")
public class KeyApiClient extends BaseApiClient implements KeySyncApiClient {

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
    private final OperationResponseValidator responseValidator;

    public KeyApiClient(WebClient webClient, TrustManager[] defaultTrustManagers,
            OperationResponseValidator responseValidator) {
        super(webClient, defaultTrustManagers);
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
            CreateKeyAttributesRequestV2Dto body) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        List<BaseAttribute> response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + CREATE_ATTRIBUTES_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntityList(BaseAttribute.class), "listCreateKeyAttributes"), request, connector);
        requireValid(responseValidator.validateAttributeList(response), connector);
        return response;
    }

    @Override
    public ResponseEntity<KeyCreationResponseV2Dto> createKey(ApiClientConnectorInfo connector,
            CreateKeyRequestV2Dto body) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        ResponseEntity<KeyCreationResponseV2Dto> response = processRequest(r -> requireResponse(r
                .uri(connector.getUrl() + BASE_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntity(KeyCreationResponseV2Dto.class), "createKey"), request, connector);
        requireValid(responseValidator.validateCreateKey(body, response), connector);
        return response;
    }

    @Override
    public KeyCreationStatusResponseV2Dto getCreateKeyStatus(ApiClientConnectorInfo connector,
            OperationTrackingRequestV2Dto body) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        KeyCreationStatusResponseV2Dto response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + CREATE_STATUS_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntity(KeyCreationStatusResponseV2Dto.class), "getCreateKeyStatus"), request, connector);
        requireValid(responseValidator.validateCreateKeyStatus(response), connector);
        return response;
    }

    @Override
    public ResponseEntity<Void> cancelCreateKey(ApiClientConnectorInfo connector, OperationTrackingRequestV2Dto body)
            throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        return processRequest(r -> requireResponse(
                r.uri(connector.getUrl() + CREATE_CANCEL_PATH).bodyValue(body).retrieve().toBodilessEntity(),
                "cancelCreateKey"), request, connector);
    }

    @Override
    public ResponseEntity<KeyOperationResponseV2Dto> destroyKey(ApiClientConnectorInfo connector,
            DestroyKeyRequestV2Dto body) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        ResponseEntity<KeyOperationResponseV2Dto> response = processRequest(r -> requireResponse(r
                .uri(connector.getUrl() + DESTROY_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntity(KeyOperationResponseV2Dto.class), "destroyKey"), request, connector);
        requireValid(responseValidator.validateDestroy(body.getExecutionMode(), response), connector);
        return response;
    }

    @Override
    public KeyDestructionStatusResponseV2Dto getDestroyKeyStatus(ApiClientConnectorInfo connector,
            OperationTrackingRequestV2Dto body) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        KeyDestructionStatusResponseV2Dto response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + DESTROY_STATUS_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntity(KeyDestructionStatusResponseV2Dto.class), "getDestroyKeyStatus"), request, connector);
        requireValid(responseValidator.validateDestroyKeyStatus(response), connector);
        return response;
    }

    @Override
    public ResponseEntity<Void> cancelDestroyKey(ApiClientConnectorInfo connector, OperationTrackingRequestV2Dto body)
            throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        return processRequest(r -> requireResponse(
                r.uri(connector.getUrl() + DESTROY_CANCEL_PATH).bodyValue(body).retrieve().toBodilessEntity(),
                "cancelDestroyKey"), request, connector);
    }

    @Override
    public List<ImportableKeyTypeV2Dto> listImportableKeyTypes(ApiClientConnectorInfo connector,
            TokenProfileScopedRequestV2Dto body) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        List<ImportableKeyTypeV2Dto> response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + IMPORT_KEY_TYPES_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntityList(ImportableKeyTypeV2Dto.class), "listImportableKeyTypes"), request, connector);
        requireValid(responseValidator.keyTransfer().validateImportableKeyTypes(response), connector);
        return response;
    }

    @Override
    public List<BaseAttribute> listImportKeyAttributes(ApiClientConnectorInfo connector,
            ImportKeyAttributesRequestV2Dto body) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        List<BaseAttribute> response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + IMPORT_ATTRIBUTES_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntityList(BaseAttribute.class), "listImportKeyAttributes"), request, connector);
        requireValid(responseValidator.validateAttributeList(response), connector);
        return response;
    }

    @Override
    public ResponseEntity<KeyCreationResponseV2Dto> importKey(ApiClientConnectorInfo connector,
            ImportKeyRequestV2Dto body) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        ResponseEntity<KeyCreationResponseV2Dto> response = processRequest(r -> requireResponse(r
                .uri(connector.getUrl() + IMPORT_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntity(KeyCreationResponseV2Dto.class), "importKey"), request, connector);
        requireValid(responseValidator.keyTransfer().validateImportKey(body, response), connector);
        return response;
    }

    @Override
    public KeyCreationStatusResponseV2Dto getImportKeyStatus(ApiClientConnectorInfo connector,
            OperationTrackingRequestV2Dto body) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        KeyCreationStatusResponseV2Dto response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + IMPORT_STATUS_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntity(KeyCreationStatusResponseV2Dto.class), "getImportKeyStatus"), request, connector);
        requireValid(responseValidator.keyTransfer().validateImportKeyStatus(response), connector);
        return response;
    }

    @Override
    public ResponseEntity<Void> cancelImportKey(ApiClientConnectorInfo connector, OperationTrackingRequestV2Dto body)
            throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        return processRequest(r -> requireResponse(
                r.uri(connector.getUrl() + IMPORT_CANCEL_PATH).bodyValue(body).retrieve().toBodilessEntity(),
                "cancelImportKey"), request, connector);
    }

    @Override
    public KeyCreationStatusResponseV2Dto getImportKeyResult(ApiClientConnectorInfo connector,
            ImportKeyResultRequestV2Dto body) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        KeyCreationStatusResponseV2Dto response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + IMPORT_RESULT_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntity(KeyCreationStatusResponseV2Dto.class), "getImportKeyResult"), request, connector);
        requireValid(responseValidator.keyTransfer().validateImportKeyStatus(response), connector);
        return response;
    }

    @Override
    public List<ExportableKeyTypeV2Dto> listExportableKeyTypes(ApiClientConnectorInfo connector,
            TokenProfileScopedRequestV2Dto body) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        List<ExportableKeyTypeV2Dto> response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + EXPORT_KEY_TYPES_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntityList(ExportableKeyTypeV2Dto.class), "listExportableKeyTypes"), request, connector);
        requireValid(responseValidator.keyTransfer().validateExportableKeyTypes(response), connector);
        return response;
    }

    @Override
    public List<BaseAttribute> listExportKeyAttributes(ApiClientConnectorInfo connector, KeyScopedRequestV2Dto body)
            throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        List<BaseAttribute> response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + EXPORT_ATTRIBUTES_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntityList(BaseAttribute.class), "listExportKeyAttributes"), request, connector);
        requireValid(responseValidator.validateAttributeList(response), connector);
        return response;
    }

    @Override
    public ExportKeyResponseV2Dto exportKey(ApiClientConnectorInfo connector, ExportKeyRequestV2Dto body)
            throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        ResponseEntity<ExportKeyResponseV2Dto> response = processRequest(r -> requireResponse(r
                .uri(connector.getUrl() + EXPORT_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntity(ExportKeyResponseV2Dto.class), "exportKey"), request, connector);
        requireValid(responseValidator.keyTransfer().validateExportKey(body, response), connector);
        return response.getBody();
    }

    @Override
    public ListKeysResponseV2Dto listKeys(ApiClientConnectorInfo connector, ListKeysRequestV2Dto body)
            throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        ListKeysResponseV2Dto response = processRequest(r -> requireBody(
                r.uri(connector.getUrl() + LIST_PATH).bodyValue(body).retrieve().toEntity(ListKeysResponseV2Dto.class),
                "listKeys"), request, connector);
        requireValid(responseValidator.validateListKeys(body, response), connector);
        return response;
    }

    @Override
    public ResolveKeysResponseV2Dto resolveKeys(ApiClientConnectorInfo connector, ResolveKeysRequestV2Dto body)
            throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        ResolveKeysResponseV2Dto response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + RESOLVE_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntity(ResolveKeysResponseV2Dto.class), "resolveKeys"), request, connector);
        requireValid(responseValidator.validateResolveKeys(body, response), connector);
        return response;
    }
}
