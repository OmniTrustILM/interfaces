package com.czertainly.api.interfaces.client.v1;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.model.connector.discovery.DiscoveryDataRequestDto;
import com.czertainly.api.model.connector.discovery.DiscoveryProviderDto;
import com.czertainly.api.model.connector.discovery.DiscoveryRequestDto;
import com.czertainly.api.clients.ApiClientConnectorInfo;

/**
 * Interface for synchronous discovery operations against connectors.
 * Implementations can use REST (direct HTTP) or MQ (proxy) communication.
 */
public interface DiscoverySyncApiClient {

    /**
     * Trigger certificate discovery on a connector.
     *
     * @param connector  Connector configuration
     * @param requestDto Discovery request parameters
     * @return Discovery provider response with status and UUID
     * @throws ConnectorException If request fails
     */
    DiscoveryProviderDto discoverCertificates(ApiClientConnectorInfo connector, DiscoveryRequestDto requestDto) throws ConnectorException;

    /**
     * Get discovery data from a connector.
     *
     * @param connector  Connector configuration
     * @param requestDto Discovery data request with pagination
     * @param uuid       Discovery UUID at the connector
     * @return Discovery provider response with certificate data
     * @throws ConnectorException If request fails
     */
    DiscoveryProviderDto getDiscoveryData(ApiClientConnectorInfo connector, DiscoveryDataRequestDto requestDto, String uuid) throws ConnectorException;

    /**
     * Remove discovery from a connector.
     *
     * @param connector Connector configuration
     * @param uuid      Discovery UUID at the connector
     * @throws ConnectorException If request fails
     */
    void removeDiscovery(ApiClientConnectorInfo connector, String uuid) throws ConnectorException;
}
