package com.otilm.api.clients.mq.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.mq.ProxyClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.interfaces.client.v2.InfoSyncApiClient;
import com.otilm.api.model.client.connector.v2.InfoResponse;

import java.util.concurrent.CompletableFuture;

/**
 * MQ-based implementation of v2 Info API client.
 */
public class InfoApiClient implements InfoSyncApiClient {

    private static final String INFO_PATH = "/v2/info";
    private static final String HTTP_METHOD_GET = "GET";

    private final ProxyClient proxyClient;

    public InfoApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public InfoResponse getConnectorInfo(ApiClientConnectorInfo connector) throws ConnectorException {
        return proxyClient.sendRequest(connector, INFO_PATH, HTTP_METHOD_GET, null, InfoResponse.class);
    }

    public CompletableFuture<InfoResponse> getConnectorInfoAsync(ApiClientConnectorInfo connector) {
        return proxyClient.sendRequestAsync(connector, INFO_PATH, HTTP_METHOD_GET, null, InfoResponse.class);
    }
}
