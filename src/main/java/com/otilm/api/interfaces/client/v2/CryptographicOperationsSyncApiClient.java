package com.otilm.api.interfaces.client.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignOperationScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignOperationStatusResponseV2Dto;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Synchronous subset of the connector-facing cryptography v2 operations API used by Core.
 */
public interface CryptographicOperationsSyncApiClient {

    List<BaseAttribute> listSignAttributes(ApiClientConnectorInfo connector,
                                           KeyScopedRequestV2Dto request)
            throws ConnectorException;

    ResponseEntity<SignDataResponseV2Dto> signData(ApiClientConnectorInfo connector, SignDataRequestV2Dto request)
            throws ConnectorException;

    SignOperationStatusResponseV2Dto getSignStatus(ApiClientConnectorInfo connector,
                                                   SignOperationScopedRequestV2Dto request)
            throws ConnectorException;

    ResponseEntity<Void> cancelSign(ApiClientConnectorInfo connector, SignOperationScopedRequestV2Dto request)
            throws ConnectorException;
}
