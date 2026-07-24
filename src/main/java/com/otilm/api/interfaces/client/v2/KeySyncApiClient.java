package com.otilm.api.interfaces.client.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Sync contract shared by REST and MQ clients for stateless cryptography v2 key creation.
 */
public interface KeySyncApiClient {

    List<@NotNull KeyRequestType> listSupportedKeyTypes(ApiClientConnectorInfo connector,
                                                        TokenProfileScopedRequestV2Dto request)
            throws ConnectorException;

    List<BaseAttribute> listCreateKeyAttributes(ApiClientConnectorInfo connector,
                                                KeyRequestType type,
                                                TokenProfileScopedRequestV2Dto request)
            throws ConnectorException;

    ResponseEntity<SecretKeyDataResponseV2Dto> createSecretKey(ApiClientConnectorInfo connector, CreateKeyRequestV2Dto request)
            throws ConnectorException;

    ResponseEntity<KeyPairDataResponseV2Dto> createKeyPair(ApiClientConnectorInfo connector, CreateKeyRequestV2Dto request)
            throws ConnectorException;

    KeyOperationStatusResponseV2Dto getCreateKeyStatus(ApiClientConnectorInfo connector,
                                                       KeyOperationStatusRequestV2Dto request)
            throws ConnectorException;

    ResponseEntity<Void> cancelCreateKey(ApiClientConnectorInfo connector,
                                         KeyOperationCancelRequestV2Dto request)
            throws ConnectorException;

    ResponseEntity<KeyDataResponseV2Dto> destroyKey(ApiClientConnectorInfo connector, DestroyKeyRequestV2Dto request)
            throws ConnectorException;

    KeyOperationStatusResponseV2Dto getDestroyKeyStatus(ApiClientConnectorInfo connector,
                                                        KeyOperationStatusRequestV2Dto request)
            throws ConnectorException;

    ResponseEntity<Void> cancelDestroyKey(ApiClientConnectorInfo connector,
                                          KeyOperationCancelRequestV2Dto request)
            throws ConnectorException;
}
