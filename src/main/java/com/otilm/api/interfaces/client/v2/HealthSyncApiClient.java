package com.otilm.api.interfaces.client.v2;

import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.client.connector.v2.HealthInfo;
import com.otilm.api.clients.ApiClientConnectorInfo;

/**
 * Sync interface for v2 Health API client operations.
 * This interface is implemented by both REST and MQ clients.
 */
public interface HealthSyncApiClient {

    HealthInfo checkHealth(ApiClientConnectorInfo connector) throws ConnectorException;

    HealthInfo checkHealthLiveness(ApiClientConnectorInfo connector) throws ConnectorException;

    HealthInfo checkHealthReadiness(ApiClientConnectorInfo connector) throws ConnectorException;
}
