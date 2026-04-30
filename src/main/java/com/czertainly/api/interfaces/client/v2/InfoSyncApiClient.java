package com.czertainly.api.interfaces.client.v2;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.model.client.connector.v2.InfoResponse;
import com.czertainly.api.clients.ApiClientConnectorInfo;

/**
 * Sync interface for v2 Info API client operations.
 * This interface is implemented by both REST and MQ clients.
 */
public interface InfoSyncApiClient {

    InfoResponse getConnectorInfo(ApiClientConnectorInfo connector) throws ConnectorException;
}
