package com.otilm.api.interfaces.client.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
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
import java.util.List;
import org.springframework.http.ResponseEntity;

/**
 * Synchronous subset of the connector-facing cryptography v2 operations API used by Core.
 */
public interface CryptographicOperationsSyncApiClient {

    List<BaseAttribute> listEncryptAttributes(ApiClientConnectorInfo connector, KeyScopedRequestV2Dto request)
            throws ConnectorException;

    EncryptDataResponseV2Dto encryptData(ApiClientConnectorInfo connector, CipherDataRequestV2Dto request)
            throws ConnectorException;

    List<BaseAttribute> listDecryptAttributes(ApiClientConnectorInfo connector, KeyScopedRequestV2Dto request)
            throws ConnectorException;

    DecryptDataResponseV2Dto decryptData(ApiClientConnectorInfo connector, CipherDataRequestV2Dto request)
            throws ConnectorException;

    List<BaseAttribute> listSignAttributes(ApiClientConnectorInfo connector, KeyScopedRequestV2Dto request)
            throws ConnectorException;

    ResponseEntity<SignDataResponseV2Dto> signData(ApiClientConnectorInfo connector, SignDataRequestV2Dto request)
            throws ConnectorException;

    SignOperationStatusResponseV2Dto getSignStatus(ApiClientConnectorInfo connector,
            OperationTrackingRequestV2Dto request) throws ConnectorException;

    ResponseEntity<Void> cancelSign(ApiClientConnectorInfo connector, OperationTrackingRequestV2Dto request)
            throws ConnectorException;

    List<BaseAttribute> listVerifyAttributes(ApiClientConnectorInfo connector, KeyScopedRequestV2Dto request)
            throws ConnectorException;

    VerifyDataResponseV2Dto verifyData(ApiClientConnectorInfo connector, VerifyDataRequestV2Dto request)
            throws ConnectorException;

    List<BaseAttribute> listRandomAttributes(ApiClientConnectorInfo connector, TokenProfileScopedRequestV2Dto request)
            throws ConnectorException;

    RandomDataResponseV2Dto randomData(ApiClientConnectorInfo connector, RandomDataRequestV2Dto request)
            throws ConnectorException;
}
