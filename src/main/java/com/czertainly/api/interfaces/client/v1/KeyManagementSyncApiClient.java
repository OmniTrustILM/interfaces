package com.czertainly.api.interfaces.client.v1;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.cryptography.key.CreateKeyRequestDto;
import com.czertainly.api.model.connector.cryptography.key.KeyDataResponseDto;
import com.czertainly.api.model.connector.cryptography.key.KeyPairDataResponseDto;
import com.czertainly.api.clients.ApiClientConnectorInfo;

import java.util.List;

public interface KeyManagementSyncApiClient {
    List<BaseAttribute> listCreateSecretKeyAttributes(ApiClientConnectorInfo connector, String uuid) throws ConnectorException;
    void validateCreateSecretKeyAttributes(ApiClientConnectorInfo connector, String uuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException;
    KeyDataResponseDto createSecretKey(ApiClientConnectorInfo connector, String uuid, CreateKeyRequestDto requestDto) throws ConnectorException;
    List<BaseAttribute> listCreateKeyPairAttributes(ApiClientConnectorInfo connector, String uuid) throws ConnectorException;
    void validateCreateKeyPairAttributes(ApiClientConnectorInfo connector, String uuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException;
    KeyPairDataResponseDto createKeyPair(ApiClientConnectorInfo connector, String uuid, CreateKeyRequestDto requestDto) throws ConnectorException;
    List<KeyDataResponseDto> listKeys(ApiClientConnectorInfo connector, String uuid) throws ConnectorException;
    KeyDataResponseDto getKey(ApiClientConnectorInfo connector, String uuid, String keyUuid) throws ConnectorException;
    void destroyKey(ApiClientConnectorInfo connector, String uuid, String keyUuid) throws ConnectorException;
}
