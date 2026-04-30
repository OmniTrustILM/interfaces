package com.czertainly.api.interfaces.client.v1;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.model.client.connector.InfoResponse;
import com.czertainly.api.clients.ApiClientConnectorInfo;

import java.util.List;

/**
 * Interface for synchronous connector API operations.
 * Implementations can use REST (direct HTTP) or MQ (proxy) communication.
 */
public interface ConnectorSyncApiClient {

    /**
     * List supported functions of a connector.
     *
     * @param connector Connector configuration
     * @return List of supported functions
     * @throws ConnectorException If request fails
     */
    List<InfoResponse> listSupportedFunctions(ApiClientConnectorInfo connector) throws ConnectorException;
}
