package com.czertainly.api.clients.mq;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.interfaces.client.v1.EntityInstanceSyncApiClient;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.entity.EntityInstanceDto;
import com.czertainly.api.model.connector.entity.EntityInstanceRequestDto;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Message queue based Entity Instance API client for connectors.
 * Uses ProxyClient to send entity instance requests via message queue
 * instead of direct REST calls.
 */
public class EntityInstanceApiClient implements EntityInstanceSyncApiClient {

    private static final String BASE_PATH = "/v1/entityProvider/entities";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";
    private static final String HTTP_METHOD_PUT = "PUT";
    private static final String HTTP_METHOD_DELETE = "DELETE";

    private final ProxyClient proxyClient;

    public EntityInstanceApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public List<EntityInstanceDto> listEntityInstances(ApiClientConnectorInfo connector) throws ConnectorException {
        EntityInstanceDto[] result = proxyClient.sendRequest(
                connector,
                BASE_PATH,
                HTTP_METHOD_GET,
                null,
                EntityInstanceDto[].class
        );
        return Arrays.asList(result);
    }

    @Override
    public EntityInstanceDto getEntityInstance(ApiClientConnectorInfo connector, String entityUuid) throws ConnectorException {
        String path = BASE_PATH + "/" + entityUuid;
        return proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_GET,
                null,
                EntityInstanceDto.class
        );
    }

    @Override
    public EntityInstanceDto createEntityInstance(ApiClientConnectorInfo connector, EntityInstanceRequestDto requestDto) throws ConnectorException {
        return proxyClient.sendRequest(
                connector,
                BASE_PATH,
                HTTP_METHOD_POST,
                requestDto,
                EntityInstanceDto.class
        );
    }

    @Override
    public EntityInstanceDto updateEntityInstance(ApiClientConnectorInfo connector, String entityUuid, EntityInstanceRequestDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/" + entityUuid;
        return proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_PUT,
                requestDto,
                EntityInstanceDto.class
        );
    }

    @Override
    public void removeEntityInstance(ApiClientConnectorInfo connector, String entityUuid) throws ConnectorException {
        String path = BASE_PATH + "/" + entityUuid;
        proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_DELETE,
                null,
                Void.class
        );
    }

    @Override
    public List<BaseAttribute> listLocationAttributes(ApiClientConnectorInfo connector, String entityUuid) throws ConnectorException {
        String path = BASE_PATH + "/" + entityUuid + "/location/attributes";
        BaseAttribute[] result = proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_GET,
                null,
                BaseAttribute[].class
        );
        return Arrays.asList(result);
    }

    @Override
    public void validateLocationAttributes(ApiClientConnectorInfo connector, String entityUuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException {
        String path = BASE_PATH + "/" + entityUuid + "/location/attributes/validate";
        proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_POST,
                attributes,
                Void.class
        );
    }

    // Async variants
    public CompletableFuture<List<EntityInstanceDto>> listEntityInstancesAsync(ApiClientConnectorInfo connector) {
        return proxyClient.sendRequestAsync(
                connector,
                BASE_PATH,
                HTTP_METHOD_GET,
                null,
                EntityInstanceDto[].class
        ).thenApply(Arrays::asList);
    }

    public CompletableFuture<EntityInstanceDto> getEntityInstanceAsync(ApiClientConnectorInfo connector, String entityUuid) {
        String path = BASE_PATH + "/" + entityUuid;
        return proxyClient.sendRequestAsync(
                connector,
                path,
                HTTP_METHOD_GET,
                null,
                EntityInstanceDto.class
        );
    }
}
