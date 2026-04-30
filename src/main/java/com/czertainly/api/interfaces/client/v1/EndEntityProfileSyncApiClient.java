package com.czertainly.api.interfaces.client.v1;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.model.common.NameAndIdDto;
import com.czertainly.api.clients.ApiClientConnectorInfo;

import java.util.List;

/**
 * Interface for synchronous end entity profile operations against connectors.
 * Implementations can use REST (direct HTTP) or MQ (proxy) communication.
 */
public interface EndEntityProfileSyncApiClient {

    /**
     * List end entity profiles for an authority instance.
     *
     * @param connector     Connector configuration
     * @param authorityUuid Authority instance UUID
     * @return List of end entity profiles
     * @throws ConnectorException If request fails
     */
    List<NameAndIdDto> listEndEntityProfiles(ApiClientConnectorInfo connector, String authorityUuid) throws ConnectorException;

    /**
     * List certificate profiles for an end entity profile.
     *
     * @param connector           Connector configuration
     * @param authorityUuid       Authority instance UUID
     * @param endEntityProfileId  End entity profile ID
     * @return List of certificate profiles
     * @throws ConnectorException If request fails
     */
    List<NameAndIdDto> listCertificateProfiles(ApiClientConnectorInfo connector, String authorityUuid, int endEntityProfileId) throws ConnectorException;

    /**
     * List CAs available in an end entity profile.
     *
     * @param connector           Connector configuration
     * @param authorityUuid       Authority instance UUID
     * @param endEntityProfileId  End entity profile ID
     * @return List of CAs
     * @throws ConnectorException If request fails
     */
    List<NameAndIdDto> listCAsInProfile(ApiClientConnectorInfo connector, String authorityUuid, int endEntityProfileId) throws ConnectorException;
}
