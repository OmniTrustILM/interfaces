package com.czertainly.api.clients.mq;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v1.DiscoverySyncApiClient;
import com.czertainly.api.model.connector.discovery.DiscoveryDataRequestDto;
import com.czertainly.api.model.connector.discovery.DiscoveryProviderDto;
import com.czertainly.api.model.connector.discovery.DiscoveryRequestDto;

import java.util.concurrent.CompletableFuture;

/**
 * Message queue based discovery API client for connectors.
 * Uses ProxyClient to send discovery requests via message queue
 * instead of direct REST calls.
 *
 * <p>This client maintains the same signature as the REST-based DiscoveryApiClient
 * to ensure compatibility with existing code.</p>
 *
 * <p>Usage: Inject this client when the connector has a proxyId set.
 * For connectors without proxyId, use the REST-based DiscoveryApiClient.</p>
 */
public class DiscoveryApiClient implements DiscoverySyncApiClient {

    private static final String DISCOVERY_BASE_PATH = "/v1/discoveryProvider/discover";
    private static final String HTTP_METHOD_POST = "POST";
    private static final String HTTP_METHOD_DELETE = "DELETE";

    private final ProxyClient proxyClient;

    /**
     * Constructor for MQ-based DiscoveryApiClient.
     *
     * @param proxyClient The proxy client to use for communication
     */
    public DiscoveryApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public DiscoveryProviderDto discoverCertificates(ApiClientConnectorInfo connector, DiscoveryRequestDto requestDto) throws ConnectorException {
        return proxyClient.sendRequest(
                connector,
                DISCOVERY_BASE_PATH,
                HTTP_METHOD_POST,
                requestDto,
                DiscoveryProviderDto.class
        );
    }

    @Override
    public DiscoveryProviderDto getDiscoveryData(ApiClientConnectorInfo connector, DiscoveryDataRequestDto requestDto, String uuid) throws ConnectorException {
        String path = DISCOVERY_BASE_PATH + "/" + uuid;
        return proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_POST,
                requestDto,
                DiscoveryProviderDto.class
        );
    }

    @Override
    public void removeDiscovery(ApiClientConnectorInfo connector, String uuid) throws ConnectorException {
        String path = DISCOVERY_BASE_PATH + "/" + uuid;
        proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_DELETE,
                null,
                Void.class
        );
    }

    /**
     * Async version of discoverCertificates.
     *
     * @param connector  Connector configuration (must have proxyId set)
     * @param requestDto Discovery request parameters
     * @return CompletableFuture that completes with DiscoveryProviderDto
     */
    public CompletableFuture<DiscoveryProviderDto> discoverCertificatesAsync(ApiClientConnectorInfo connector, DiscoveryRequestDto requestDto) {
        return proxyClient.sendRequestAsync(
                connector,
                DISCOVERY_BASE_PATH,
                HTTP_METHOD_POST,
                requestDto,
                DiscoveryProviderDto.class
        );
    }

    /**
     * Async version of getDiscoveryData.
     *
     * @param connector  Connector configuration (must have proxyId set)
     * @param requestDto Discovery data request with pagination
     * @param uuid       Discovery UUID at the connector
     * @return CompletableFuture that completes with DiscoveryProviderDto
     */
    public CompletableFuture<DiscoveryProviderDto> getDiscoveryDataAsync(ApiClientConnectorInfo connector, DiscoveryDataRequestDto requestDto, String uuid) {
        String path = DISCOVERY_BASE_PATH + "/" + uuid;
        return proxyClient.sendRequestAsync(
                connector,
                path,
                HTTP_METHOD_POST,
                requestDto,
                DiscoveryProviderDto.class
        );
    }
}
