package com.czertainly.api.clients.mq.v2;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.clients.mq.ProxyClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v2.HealthSyncApiClient;
import com.czertainly.api.model.client.connector.v2.HealthInfo;

import java.util.concurrent.CompletableFuture;

/**
 * MQ-based implementation of v2 Health API client.
 */
public class HealthApiClient implements HealthSyncApiClient {

    private static final String HEALTH_PATH = "/v2/health";
    private static final String HEALTH_LIVENESS_PATH = "/v2/health/liveness";
    private static final String HEALTH_READINESS_PATH = "/v2/health/readiness";
    private static final String HTTP_METHOD_GET = "GET";

    private final ProxyClient proxyClient;

    public HealthApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public HealthInfo checkHealth(ApiClientConnectorInfo connector) throws ConnectorException {
        return proxyClient.sendRequest(connector, HEALTH_PATH, HTTP_METHOD_GET, null, HealthInfo.class);
    }

    @Override
    public HealthInfo checkHealthLiveness(ApiClientConnectorInfo connector) throws ConnectorException {
        return proxyClient.sendRequest(connector, HEALTH_LIVENESS_PATH, HTTP_METHOD_GET, null, HealthInfo.class);
    }

    @Override
    public HealthInfo checkHealthReadiness(ApiClientConnectorInfo connector) throws ConnectorException {
        return proxyClient.sendRequest(connector, HEALTH_READINESS_PATH, HTTP_METHOD_GET, null, HealthInfo.class);
    }

    public CompletableFuture<HealthInfo> checkHealthAsync(ApiClientConnectorInfo connector) {
        return proxyClient.sendRequestAsync(connector, HEALTH_PATH, HTTP_METHOD_GET, null, HealthInfo.class);
    }

    public CompletableFuture<HealthInfo> checkHealthLivenessAsync(ApiClientConnectorInfo connector) {
        return proxyClient.sendRequestAsync(connector, HEALTH_LIVENESS_PATH, HTTP_METHOD_GET, null, HealthInfo.class);
    }

    public CompletableFuture<HealthInfo> checkHealthReadinessAsync(ApiClientConnectorInfo connector) {
        return proxyClient.sendRequestAsync(connector, HEALTH_READINESS_PATH, HTTP_METHOD_GET, null, HealthInfo.class);
    }
}
