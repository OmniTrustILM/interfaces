package com.czertainly.api.interfaces.client.v1.signing;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;

import java.util.List;

public interface SignatureFormatterSyncApiClient {

    List<BaseAttribute> listFormatterAttributes(ApiClientConnectorInfo connector) throws ConnectorException;

}
