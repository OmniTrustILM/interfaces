package com.czertainly.api.clients.mq;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v1.CertificateSyncApiClient;
import com.czertainly.api.model.core.authority.CertRevocationDto;
import com.czertainly.api.model.core.authority.CertificateSignRequestDto;
import com.czertainly.api.model.core.authority.CertificateSignResponseDto;

import java.util.concurrent.CompletableFuture;

/**
 * Message queue based certificate API client for connectors.
 * Uses ProxyClient to send certificate requests via message queue
 * instead of direct REST calls.
 *
 * <p>This client maintains the same signature as the REST-based CertificateApiClient
 * to ensure compatibility with existing code.</p>
 *
 * <p>Usage: Inject this client when the connector has a proxyId set.
 * For connectors without proxyId, use the REST-based CertificateApiClient.</p>
 */
public class CertificateApiClient implements CertificateSyncApiClient {

    private static final String BASE_PATH = "/v1/authorityProvider/authorities";
    private static final String HTTP_METHOD_POST = "POST";

    private final ProxyClient proxyClient;

    /**
     * Constructor for MQ-based CertificateApiClient.
     *
     * @param proxyClient The proxy client to use for communication
     */
    public CertificateApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public CertificateSignResponseDto issueCertificate(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName, CertificateSignRequestDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/" + authorityUuid + "/endEntityProfiles/" + endEntityProfileName + "/certificates/issue";
        return proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_POST,
                requestDto,
                CertificateSignResponseDto.class
        );
    }

    @Override
    public void revokeCertificate(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName, CertRevocationDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/" + authorityUuid + "/endEntityProfiles/" + endEntityProfileName + "/certificates/revoke";
        proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_POST,
                requestDto,
                Void.class
        );
    }

    /**
     * Async version of issueCertificate.
     *
     * @param connector            Connector configuration (must have proxyId set)
     * @param authorityUuid        Authority instance UUID
     * @param endEntityProfileName End entity profile name
     * @param requestDto           Certificate sign request
     * @return CompletableFuture that completes with CertificateSignResponseDto
     */
    public CompletableFuture<CertificateSignResponseDto> issueCertificateAsync(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName, CertificateSignRequestDto requestDto) {
        String path = BASE_PATH + "/" + authorityUuid + "/endEntityProfiles/" + endEntityProfileName + "/certificates/issue";
        return proxyClient.sendRequestAsync(
                connector,
                path,
                HTTP_METHOD_POST,
                requestDto,
                CertificateSignResponseDto.class
        );
    }
}
