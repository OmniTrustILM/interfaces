package com.czertainly.api.interfaces.client.v1.secret;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;

import java.util.List;

public interface VaultSyncApiClient {

    void checkVaultConnection(ApiClientConnectorInfo connector, List<RequestAttribute> attributes) throws ConnectorException;

    List<BaseAttribute> listVaultAttributes(ApiClientConnectorInfo connector) throws ConnectorException;

    List<BaseAttribute> listVaultProfileAttributes(ApiClientConnectorInfo connector, List<RequestAttribute> attributes) throws ConnectorException;
}
