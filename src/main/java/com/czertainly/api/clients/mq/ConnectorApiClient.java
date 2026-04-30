package com.czertainly.api.clients.mq;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.interfaces.client.v1.ConnectorSyncApiClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.model.client.connector.InfoResponse;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Message queue based connector API client.
 * Uses ProxyClient to send requests via message queue instead of direct REST calls.
 *
 * <p>This client maintains the same signature as the REST-based ConnectorApiClient
 * to ensure compatibility with existing code.</p>
 */
public class ConnectorApiClient implements ConnectorSyncApiClient {

    private static final String CONNECTOR_BASE_CONTEXT = "/v1";
    private static final String HTTP_METHOD_GET = "GET";

    private final ProxyClient proxyClient;

    public ConnectorApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    /**
     * List supported functions of a connector via proxy message queue.
     *
     * @param connector Connector configuration (must have proxyId set)
     * @return List of supported functions
     * @throws ConnectorException If request fails
     */
    @Override
    public List<InfoResponse> listSupportedFunctions(ApiClientConnectorInfo connector) throws ConnectorException {
        InfoResponse[] result = proxyClient.sendRequest(
                connector,
                CONNECTOR_BASE_CONTEXT,
                HTTP_METHOD_GET,
                null,
                InfoResponse[].class
        );
        return Arrays.asList(result);
    }

    /**
     * Async version of listSupportedFunctions.
     *
     * @param connector Connector configuration (must have proxyId set)
     * @return CompletableFuture that completes with list of supported functions
     */
    public CompletableFuture<List<InfoResponse>> listSupportedFunctionsAsync(ApiClientConnectorInfo connector) {
        return proxyClient.sendRequestAsync(
                connector,
                CONNECTOR_BASE_CONTEXT,
                HTTP_METHOD_GET,
                null,
                InfoResponse[].class
        ).thenApply(Arrays::asList);
    }
}