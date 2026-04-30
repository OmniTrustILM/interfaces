package com.czertainly.api.clients.mq;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.interfaces.client.v1.HealthSyncApiClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.model.common.HealthDto;

import java.util.concurrent.CompletableFuture;

/**
 * Message queue based health API client for connectors.
 * Uses ProxyClient to send health check requests via message queue
 * instead of direct REST calls.
 *
 * <p>This client maintains the same signature as the REST-based HealthApiClient
 * to ensure compatibility with existing code.</p>
 *
 * <p>Usage: Inject this client when the connector has a proxyId set.
 * For connectors without proxyId, use the REST-based HealthApiClient.</p>
 */
public class HealthApiClient implements HealthSyncApiClient {

    private static final String HEALTH_PATH = "/v1/health";
    private static final String HTTP_METHOD_GET = "GET";

    private final ProxyClient proxyClient;

    /**
     * Constructor for MQ-based HealthApiClient.
     *
     * @param proxyClient The proxy client to use for communication
     */
    public HealthApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    /**
     * Check health of a connector via proxy message queue.
     * Maintains same signature as REST-based HealthApiClient.
     *
     * @param connector Connector configuration (must have proxyId set)
     * @return HealthDto containing connector health status
     * @throws ConnectorException If health check fails
     * @throws IllegalArgumentException If connector.proxyId is null
     */
    @Override
    public HealthDto checkHealth(ApiClientConnectorInfo connector) throws ConnectorException {
        return proxyClient.sendRequest(
                connector,
                HEALTH_PATH,
                HTTP_METHOD_GET,
                null,  // No request body for GET
                HealthDto.class
        );
    }

    /**
     * Async version of health check.
     *
     * @param connector Connector configuration (must have proxyId set)
     * @return CompletableFuture that completes with HealthDto
     */
    public CompletableFuture<HealthDto> checkHealthAsync(ApiClientConnectorInfo connector) {
        return proxyClient.sendRequestAsync(
                connector,
                HEALTH_PATH,
                HTTP_METHOD_GET,
                null,
                HealthDto.class
        );
    }
}
