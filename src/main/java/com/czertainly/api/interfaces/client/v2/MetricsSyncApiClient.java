package com.czertainly.api.interfaces.client.v2;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.clients.ApiClientConnectorInfo;

/**
 * Sync interface for v2 Metrics API client operations.
 * This interface is implemented by both REST and MQ clients.
 */
public interface MetricsSyncApiClient {

    String getMetrics(ApiClientConnectorInfo connector) throws ConnectorException;
}
