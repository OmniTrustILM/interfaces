package com.otilm.api.interfaces.client.v1.secret;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.BaseAttribute;

import java.util.List;

public interface VaultSyncApiClient {

    void checkVaultConnection(ApiClientConnectorInfo connector, List<RequestAttribute> attributes) throws ConnectorException;

    List<BaseAttribute> listVaultAttributes(ApiClientConnectorInfo connector) throws ConnectorException;

    List<BaseAttribute> listVaultProfileAttributes(ApiClientConnectorInfo connector, List<RequestAttribute> attributes) throws ConnectorException;
}
