package com.czertainly.api.clients.mq;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v1.NotificationInstanceSyncApiClient;
import com.czertainly.api.model.common.attribute.common.DataAttribute;
import com.czertainly.api.model.connector.notification.NotificationProviderInstanceDto;
import com.czertainly.api.model.connector.notification.NotificationProviderInstanceRequestDto;
import com.czertainly.api.model.connector.notification.NotificationProviderNotifyRequestDto;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * MQ-based implementation of Notification Instance API client.
 */
public class NotificationInstanceApiClient implements NotificationInstanceSyncApiClient {

    private static final String BASE_PATH = "/v1/notificationProvider/notifications";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";
    private static final String HTTP_METHOD_PUT = "PUT";
    private static final String HTTP_METHOD_DELETE = "DELETE";

    private final ProxyClient proxyClient;

    public NotificationInstanceApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public List<NotificationProviderInstanceDto> listNotificationInstances(ApiClientConnectorInfo connector) throws ConnectorException {
        NotificationProviderInstanceDto[] result = proxyClient.sendRequest(connector, BASE_PATH, HTTP_METHOD_GET, null, NotificationProviderInstanceDto[].class);
        return Arrays.asList(result);
    }

    @Override
    public NotificationProviderInstanceDto getNotificationInstance(ApiClientConnectorInfo connector, String uuid) throws ConnectorException {
        String path = BASE_PATH + "/" + uuid;
        return proxyClient.sendRequest(connector, path, HTTP_METHOD_GET, null, NotificationProviderInstanceDto.class);
    }

    @Override
    public NotificationProviderInstanceDto createNotificationInstance(ApiClientConnectorInfo connector, NotificationProviderInstanceRequestDto requestDto) throws ConnectorException {
        return proxyClient.sendRequest(connector, BASE_PATH, HTTP_METHOD_POST, requestDto, NotificationProviderInstanceDto.class);
    }

    @Override
    public NotificationProviderInstanceDto updateNotificationInstance(ApiClientConnectorInfo connector, String uuid, NotificationProviderInstanceRequestDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/" + uuid;
        return proxyClient.sendRequest(connector, path, HTTP_METHOD_PUT, requestDto, NotificationProviderInstanceDto.class);
    }

    @Override
    public void removeNotificationInstance(ApiClientConnectorInfo connector, String uuid) throws ConnectorException {
        String path = BASE_PATH + "/" + uuid;
        proxyClient.sendRequest(connector, path, HTTP_METHOD_DELETE, null, Void.class);
    }

    @Override
    public void sendNotification(ApiClientConnectorInfo connector, String uuid, NotificationProviderNotifyRequestDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/" + uuid + "/notify";
        proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, requestDto, Void.class);
    }

    @Override
    public List<DataAttribute> listMappingAttributes(ApiClientConnectorInfo connector, String kind) throws ConnectorException {
        String path = "/v1/notificationProvider/" + kind + "/attributes/mapping";
        DataAttribute[] result = proxyClient.sendRequest(connector, path, HTTP_METHOD_GET, null, DataAttribute[].class);
        return Arrays.asList(result);
    }

    // Async variants
    public CompletableFuture<List<NotificationProviderInstanceDto>> listNotificationInstancesAsync(ApiClientConnectorInfo connector) {
        return proxyClient.sendRequestAsync(connector, BASE_PATH, HTTP_METHOD_GET, null, NotificationProviderInstanceDto[].class)
                .thenApply(Arrays::asList);
    }

    public CompletableFuture<NotificationProviderInstanceDto> getNotificationInstanceAsync(ApiClientConnectorInfo connector, String uuid) {
        String path = BASE_PATH + "/" + uuid;
        return proxyClient.sendRequestAsync(connector, path, HTTP_METHOD_GET, null, NotificationProviderInstanceDto.class);
    }

    public CompletableFuture<Void> sendNotificationAsync(ApiClientConnectorInfo connector, String uuid, NotificationProviderNotifyRequestDto requestDto) {
        String path = BASE_PATH + "/" + uuid + "/notify";
        return proxyClient.sendRequestAsync(connector, path, HTTP_METHOD_POST, requestDto, Void.class);
    }
}
