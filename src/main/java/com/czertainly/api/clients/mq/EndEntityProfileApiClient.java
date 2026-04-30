package com.czertainly.api.clients.mq;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v1.EndEntityProfileSyncApiClient;
import com.czertainly.api.model.common.NameAndIdDto;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Message queue based end entity profile API client for connectors.
 * Uses ProxyClient to send requests via message queue instead of direct REST calls.
 *
 * <p>This client maintains the same signature as the REST-based EndEntityProfileApiClient
 * to ensure compatibility with existing code.</p>
 *
 * <p>Usage: Inject this client when the connector has a proxyId set.
 * For connectors without proxyId, use the REST-based EndEntityProfileApiClient.</p>
 */
public class EndEntityProfileApiClient implements EndEntityProfileSyncApiClient {

    private static final String BASE_PATH = "/v1/authorityProvider/authorities";
    private static final String HTTP_METHOD_GET = "GET";

    private final ProxyClient proxyClient;

    /**
     * Constructor for MQ-based EndEntityProfileApiClient.
     *
     * @param proxyClient The proxy client to use for communication
     */
    public EndEntityProfileApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public List<NameAndIdDto> listEndEntityProfiles(ApiClientConnectorInfo connector, String authorityUuid) throws ConnectorException {
        String path = BASE_PATH + "/" + authorityUuid + "/endEntityProfiles";
        NameAndIdDto[] result = proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_GET,
                null,
                NameAndIdDto[].class
        );
        return Arrays.asList(result);
    }

    @Override
    public List<NameAndIdDto> listCertificateProfiles(ApiClientConnectorInfo connector, String authorityUuid, int endEntityProfileId) throws ConnectorException {
        String path = BASE_PATH + "/" + authorityUuid + "/endEntityProfiles/" + endEntityProfileId + "/certificateprofiles";
        NameAndIdDto[] result = proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_GET,
                null,
                NameAndIdDto[].class
        );
        return Arrays.asList(result);
    }

    @Override
    public List<NameAndIdDto> listCAsInProfile(ApiClientConnectorInfo connector, String authorityUuid, int endEntityProfileId) throws ConnectorException {
        String path = BASE_PATH + "/" + authorityUuid + "/endEntityProfiles/" + endEntityProfileId + "/cas";
        NameAndIdDto[] result = proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_GET,
                null,
                NameAndIdDto[].class
        );
        return Arrays.asList(result);
    }

    /**
     * Async version of listEndEntityProfiles.
     *
     * @param connector     Connector configuration (must have proxyId set)
     * @param authorityUuid Authority instance UUID
     * @return CompletableFuture that completes with list of end entity profiles
     */
    public CompletableFuture<List<NameAndIdDto>> listEndEntityProfilesAsync(ApiClientConnectorInfo connector, String authorityUuid) {
        String path = BASE_PATH + "/" + authorityUuid + "/endEntityProfiles";
        return proxyClient.sendRequestAsync(
                connector,
                path,
                HTTP_METHOD_GET,
                null,
                NameAndIdDto[].class
        ).thenApply(Arrays::asList);
    }
}
