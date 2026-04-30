package com.czertainly.api.interfaces.client.v1;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.model.common.HealthDto;
import com.czertainly.api.clients.ApiClientConnectorInfo;

/**
 * Interface for synchronous health check operations against connectors.
 * Implementations can use REST (direct HTTP) or MQ (proxy) communication.
 */
public interface HealthSyncApiClient {

    /**
     * Check health status of a connector.
     *
     * @param connector Connector configuration
     * @return Health status
     * @throws ConnectorException If request fails
     */
    HealthDto checkHealth(ApiClientConnectorInfo connector) throws ConnectorException;
}
