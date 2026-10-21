package com.otilm.api.interfaces.client.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
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
import com.otilm.api.model.connector.cryptography.v2.key.KeyOperationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListKeysRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListKeysResponseV2Dto;
import java.util.List;
import org.springframework.http.ResponseEntity;

/**
 * Sync contract shared by REST and MQ clients for stateless cryptography v2 key operations.
 */
public interface KeySyncApiClient {

    List<BaseAttribute> listCreateKeyAttributes(ApiClientConnectorInfo connector,
            CreateKeyAttributesRequestV2Dto request) throws ConnectorException;

    ResponseEntity<KeyCreationResponseV2Dto> createKey(ApiClientConnectorInfo connector, CreateKeyRequestV2Dto request)
            throws ConnectorException;

    KeyCreationStatusResponseV2Dto getCreateKeyStatus(ApiClientConnectorInfo connector,
            OperationTrackingRequestV2Dto request) throws ConnectorException;

    ResponseEntity<Void> cancelCreateKey(ApiClientConnectorInfo connector, OperationTrackingRequestV2Dto request)
            throws ConnectorException;

    ResponseEntity<KeyOperationResponseV2Dto> destroyKey(ApiClientConnectorInfo connector,
            DestroyKeyRequestV2Dto request) throws ConnectorException;

    KeyDestructionStatusResponseV2Dto getDestroyKeyStatus(ApiClientConnectorInfo connector,
            OperationTrackingRequestV2Dto request) throws ConnectorException;

    ResponseEntity<Void> cancelDestroyKey(ApiClientConnectorInfo connector, OperationTrackingRequestV2Dto request)
            throws ConnectorException;

    List<ImportableKeyTypeV2Dto> listImportableKeyTypes(ApiClientConnectorInfo connector,
            TokenProfileScopedRequestV2Dto request) throws ConnectorException;

    List<BaseAttribute> listImportKeyAttributes(ApiClientConnectorInfo connector,
            ImportKeyAttributesRequestV2Dto request) throws ConnectorException;

    ResponseEntity<KeyCreationResponseV2Dto> importKey(ApiClientConnectorInfo connector, ImportKeyRequestV2Dto request)
            throws ConnectorException;

    KeyCreationStatusResponseV2Dto getImportKeyStatus(ApiClientConnectorInfo connector,
            OperationTrackingRequestV2Dto request) throws ConnectorException;

    ResponseEntity<Void> cancelImportKey(ApiClientConnectorInfo connector, OperationTrackingRequestV2Dto request)
            throws ConnectorException;

    KeyCreationStatusResponseV2Dto getImportKeyResult(ApiClientConnectorInfo connector,
            ImportKeyResultRequestV2Dto request) throws ConnectorException;

    List<ExportableKeyTypeV2Dto> listExportableKeyTypes(ApiClientConnectorInfo connector,
            TokenProfileScopedRequestV2Dto request) throws ConnectorException;

    List<BaseAttribute> listExportKeyAttributes(ApiClientConnectorInfo connector, KeyScopedRequestV2Dto request)
            throws ConnectorException;

    ExportKeyResponseV2Dto exportKey(ApiClientConnectorInfo connector, ExportKeyRequestV2Dto request)
            throws ConnectorException;

    ListKeysResponseV2Dto listKeys(ApiClientConnectorInfo connector, ListKeysRequestV2Dto request)
            throws ConnectorException;
}
