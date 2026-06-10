package com.otilm.api.interfaces.client.v2;

import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.client.connector.v2.InfoResponse;
import com.otilm.api.clients.ApiClientConnectorInfo;

/**
 * Sync interface for v2 Info API client operations.
 * This interface is implemented by both REST and MQ clients.
 */
public interface InfoSyncApiClient {

    InfoResponse getConnectorInfo(ApiClientConnectorInfo connector) throws ConnectorException;
}
