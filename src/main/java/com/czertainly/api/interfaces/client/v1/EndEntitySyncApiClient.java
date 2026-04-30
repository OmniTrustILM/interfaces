package com.czertainly.api.interfaces.client.v1;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.model.core.authority.AddEndEntityRequestDto;
import com.czertainly.api.model.core.authority.EditEndEntityRequestDto;
import com.czertainly.api.model.core.authority.EndEntityDto;
import com.czertainly.api.clients.ApiClientConnectorInfo;

import java.util.List;

/**
 * Synchronous API client interface for End Entity operations.
 * This interface provides an abstraction layer allowing both REST and MQ implementations.
 */
public interface EndEntitySyncApiClient {

    /**
     * List all end entities for a given authority and end entity profile.
     *
     * @param connector Connector to use
     * @param authorityUuid Authority instance UUID
     * @param endEntityProfileName End entity profile name
     * @return List of end entities
     * @throws ConnectorException If there is an error communicating with the connector
     */
    List<EndEntityDto> listEntities(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName) throws ConnectorException;

    /**
     * Get a specific end entity.
     *
     * @param connector Connector to use
     * @param authorityUuid Authority instance UUID
     * @param endEntityProfileName End entity profile name
     * @param endEntityName End entity name
     * @return End entity details
     * @throws ConnectorException If there is an error communicating with the connector
     */
    EndEntityDto getEndEntity(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName, String endEntityName) throws ConnectorException;

    /**
     * Create a new end entity.
     *
     * @param connector Connector to use
     * @param authorityUuid Authority instance UUID
     * @param endEntityProfileName End entity profile name
     * @param requestDto End entity creation request
     * @throws ConnectorException If there is an error communicating with the connector
     */
    void createEndEntity(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName, AddEndEntityRequestDto requestDto) throws ConnectorException;

    /**
     * Update an existing end entity.
     *
     * @param connector Connector to use
     * @param authorityUuid Authority instance UUID
     * @param endEntityProfileName End entity profile name
     * @param endEntityName End entity name
     * @param requestDto End entity update request
     * @throws ConnectorException If there is an error communicating with the connector
     */
    void updateEndEntity(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName, String endEntityName, EditEndEntityRequestDto requestDto) throws ConnectorException;

    /**
     * Revoke and delete an end entity.
     *
     * @param connector Connector to use
     * @param authorityUuid Authority instance UUID
     * @param endEntityProfileName End entity profile name
     * @param endEntityName End entity name
     * @throws ConnectorException If there is an error communicating with the connector
     */
    void revokeAndDeleteEndEntity(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName, String endEntityName) throws ConnectorException;

    /**
     * Reset password for an end entity.
     *
     * @param connector Connector to use
     * @param authorityUuid Authority instance UUID
     * @param endEntityProfileName End entity profile name
     * @param endEntityName End entity name
     * @throws ConnectorException If there is an error communicating with the connector
     */
    void resetPassword(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName, String endEntityName) throws ConnectorException;
}
