package com.czertainly.api.clients.mq;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v1.EndEntitySyncApiClient;
import com.czertainly.api.model.core.authority.AddEndEntityRequestDto;
import com.czertainly.api.model.core.authority.EditEndEntityRequestDto;
import com.czertainly.api.model.core.authority.EndEntityDto;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * MQ-based implementation of End Entity API client.
 */
public class EndEntityApiClient implements EndEntitySyncApiClient {

    private static final String BASE_PATH = "/v1/authorityProvider/authorities";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";
    private static final String HTTP_METHOD_PUT = "PUT";
    private static final String HTTP_METHOD_DELETE = "DELETE";

    private final ProxyClient proxyClient;

    public EndEntityApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public List<EndEntityDto> listEntities(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName) throws ConnectorException {
        String path = BASE_PATH + "/" + authorityUuid + "/endEntityProfiles/" + endEntityProfileName + "/endEntities";
        EndEntityDto[] result = proxyClient.sendRequest(connector, path, HTTP_METHOD_GET, null, EndEntityDto[].class);
        return Arrays.asList(result);
    }

    @Override
    public EndEntityDto getEndEntity(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName, String endEntityName) throws ConnectorException {
        String path = BASE_PATH + "/" + authorityUuid + "/endEntityProfiles/" + endEntityProfileName + "/endEntities/" + endEntityName;
        return proxyClient.sendRequest(connector, path, HTTP_METHOD_GET, null, EndEntityDto.class);
    }

    @Override
    public void createEndEntity(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName, AddEndEntityRequestDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/" + authorityUuid + "/endEntityProfiles/" + endEntityProfileName + "/endEntities";
        proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, requestDto, Void.class);
    }

    @Override
    public void updateEndEntity(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName, String endEntityName, EditEndEntityRequestDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/" + authorityUuid + "/endEntityProfiles/" + endEntityProfileName + "/endEntities/" + endEntityName;
        proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, requestDto, Void.class);
    }

    @Override
    public void revokeAndDeleteEndEntity(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName, String endEntityName) throws ConnectorException {
        String path = BASE_PATH + "/" + authorityUuid + "/endEntityProfiles/" + endEntityProfileName + "/endEntities/" + endEntityName;
        proxyClient.sendRequest(connector, path, HTTP_METHOD_DELETE, null, Void.class);
    }

    @Override
    public void resetPassword(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName, String endEntityName) throws ConnectorException {
        String path = BASE_PATH + "/" + authorityUuid + "/endEntityProfiles/" + endEntityProfileName + "/endEntities/" + endEntityName + "/resetPassword";
        proxyClient.sendRequest(connector, path, HTTP_METHOD_PUT, null, Void.class);
    }

    // Async variants
    public CompletableFuture<List<EndEntityDto>> listEntitiesAsync(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName) {
        String path = BASE_PATH + "/" + authorityUuid + "/endEntityProfiles/" + endEntityProfileName + "/endEntities";
        return proxyClient.sendRequestAsync(connector, path, HTTP_METHOD_GET, null, EndEntityDto[].class)
                .thenApply(Arrays::asList);
    }

    public CompletableFuture<EndEntityDto> getEndEntityAsync(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName, String endEntityName) {
        String path = BASE_PATH + "/" + authorityUuid + "/endEntityProfiles/" + endEntityProfileName + "/endEntities/" + endEntityName;
        return proxyClient.sendRequestAsync(connector, path, HTTP_METHOD_GET, null, EndEntityDto.class);
    }
}
