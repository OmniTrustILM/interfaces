package com.czertainly.api.interfaces.client.v1;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.cryptography.token.TokenInstanceDto;
import com.czertainly.api.model.connector.cryptography.token.TokenInstanceRequestDto;
import com.czertainly.api.model.connector.cryptography.token.TokenInstanceStatusDto;
import com.czertainly.api.clients.ApiClientConnectorInfo;

import java.util.List;

public interface TokenInstanceSyncApiClient {
    List<TokenInstanceDto> listTokenInstances(ApiClientConnectorInfo connector) throws ConnectorException;
    TokenInstanceDto getTokenInstance(ApiClientConnectorInfo connector, String uuid) throws ConnectorException;
    TokenInstanceDto createTokenInstance(ApiClientConnectorInfo connector, TokenInstanceRequestDto requestDto) throws ConnectorException;
    TokenInstanceDto updateTokenInstance(ApiClientConnectorInfo connector, String uuid, TokenInstanceRequestDto requestDto) throws ConnectorException;
    void removeTokenInstance(ApiClientConnectorInfo connector, String uuid) throws ConnectorException;
    TokenInstanceStatusDto getTokenInstanceStatus(ApiClientConnectorInfo connector, String uuid) throws ConnectorException;
    List<BaseAttribute> listTokenProfileAttributes(ApiClientConnectorInfo connector, String uuid) throws ConnectorException;
    void validateTokenProfileAttributes(ApiClientConnectorInfo connector, String uuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException;
    List<BaseAttribute> listTokenInstanceActivationAttributes(ApiClientConnectorInfo connector, String uuid) throws ConnectorException;
    void validateTokenInstanceActivationAttributes(ApiClientConnectorInfo connector, String uuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException;
    void activateTokenInstance(ApiClientConnectorInfo connector, String uuid, List<RequestAttribute> attributes) throws ConnectorException;
    void deactivateTokenInstance(ApiClientConnectorInfo connector, String uuid) throws ConnectorException;
}
