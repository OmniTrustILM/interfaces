package com.czertainly.api.interfaces.client.v1;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.cryptography.operations.*;
import com.czertainly.api.clients.ApiClientConnectorInfo;

import java.util.List;

public interface CryptographicOperationsSyncApiClient {
    EncryptDataResponseDto encryptData(ApiClientConnectorInfo connector, String uuid, String keyUuid, CipherDataRequestDto requestDto) throws ConnectorException;
    DecryptDataResponseDto decryptData(ApiClientConnectorInfo connector, String uuid, String keyUuid, CipherDataRequestDto requestDto) throws ConnectorException;
    SignDataResponseDto signData(ApiClientConnectorInfo connector, String uuid, String keyUuid, SignDataRequestDto requestDto) throws ConnectorException;
    VerifyDataResponseDto verifyData(ApiClientConnectorInfo connector, String uuid, String keyUuid, VerifyDataRequestDto requestDto) throws ConnectorException;
    List<BaseAttribute> listRandomAttributes(ApiClientConnectorInfo connector, String uuid) throws ConnectorException;
    void validateRandomAttributes(ApiClientConnectorInfo connector, String uuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException;
    RandomDataResponseDto randomData(ApiClientConnectorInfo connector, String uuid, RandomDataRequestDto requestDto) throws ConnectorException;
}
