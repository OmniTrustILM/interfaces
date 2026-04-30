package com.czertainly.api.clients.mq.v2;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.clients.mq.ProxyClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v2.MetricsSyncApiClient;

import java.util.concurrent.CompletableFuture;

/**
 * MQ-based implementation of v2 Metrics API client.
 */
public class MetricsApiClient implements MetricsSyncApiClient {

    private static final String METRICS_PATH = "/v1/metrics";
    private static final String HTTP_METHOD_GET = "GET";

    private final ProxyClient proxyClient;

    public MetricsApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public String getMetrics(ApiClientConnectorInfo connector) throws ConnectorException {
        return proxyClient.sendRequest(connector, METRICS_PATH, HTTP_METHOD_GET, null, String.class);
    }

    public CompletableFuture<String> getMetricsAsync(ApiClientConnectorInfo connector) {
        return proxyClient.sendRequestAsync(connector, METRICS_PATH, HTTP_METHOD_GET, null, String.class);
    }
}
