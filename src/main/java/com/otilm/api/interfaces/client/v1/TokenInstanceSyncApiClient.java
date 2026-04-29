package com.otilm.api.interfaces.client.v1;

import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.token.TokenInstanceDto;
import com.otilm.api.model.connector.cryptography.token.TokenInstanceRequestDto;
import com.otilm.api.model.connector.cryptography.token.TokenInstanceStatusDto;
import com.otilm.api.clients.ApiClientConnectorInfo;

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
