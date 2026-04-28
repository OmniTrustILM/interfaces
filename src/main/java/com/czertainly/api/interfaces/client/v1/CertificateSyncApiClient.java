package com.czertainly.api.interfaces.client.v1;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.model.core.authority.CertRevocationDto;
import com.czertainly.api.model.core.authority.CertificateSignRequestDto;
import com.czertainly.api.model.core.authority.CertificateSignResponseDto;
import com.czertainly.api.clients.ApiClientConnectorInfo;

/**
 * Interface for synchronous certificate operations against connectors.
 * Implementations can use REST (direct HTTP) or MQ (proxy) communication.
 */
public interface CertificateSyncApiClient {

    /**
     * Issue a certificate via authority provider.
     *
     * @param connector            Connector configuration
     * @param authorityUuid        Authority instance UUID
     * @param endEntityProfileName End entity profile name
     * @param requestDto           Certificate sign request
     * @return Certificate sign response with issued certificate
     * @throws ConnectorException If request fails
     */
    CertificateSignResponseDto issueCertificate(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName, CertificateSignRequestDto requestDto) throws ConnectorException;

    /**
     * Revoke a certificate via authority provider.
     *
     * @param connector            Connector configuration
     * @param authorityUuid        Authority instance UUID
     * @param endEntityProfileName End entity profile name
     * @param requestDto           Certificate revocation request
     * @throws ConnectorException If request fails
     */
    void revokeCertificate(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName, CertRevocationDto requestDto) throws ConnectorException;
}
