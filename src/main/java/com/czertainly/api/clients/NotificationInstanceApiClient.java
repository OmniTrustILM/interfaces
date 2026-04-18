package com.czertainly.api.clients;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.model.common.attribute.common.DataAttribute;
import com.czertainly.api.model.connector.notification.NotificationProviderInstanceDto;
import com.czertainly.api.model.connector.notification.NotificationProviderInstanceRequestDto;
import com.czertainly.api.model.connector.notification.NotificationProviderNotifyRequestDto;
import com.czertainly.api.model.core.connector.ConnectorApiClientDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.net.ssl.TrustManager;
import java.util.List;
import java.util.Objects;

public class NotificationInstanceApiClient extends BaseApiClient {

    private static final String NOTIFICATION_INSTANCE_BASE_CONTEXT = "/v1/notificationProvider/notifications";
    private static final String NOTIFICATION_INSTANCE_IDENTIFIED_CONTEXT = NOTIFICATION_INSTANCE_BASE_CONTEXT + "/{uuid}";
    private static final String NOTIFICATION_INSTANCE_IDENTIFIED_SEND_CONTEXT = NOTIFICATION_INSTANCE_BASE_CONTEXT + "/{uuid}/notify";

    private static final String NOTIFICATION_INSTANCE_MAPPING_ATTRIBUTES_CONTEXT = "/v1/notificationProvider/{kind}/attributes/mapping";

    public NotificationInstanceApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        this.webClient = webClient;
        this.defaultTrustManagers = defaultTrustManagers;
    }

    public List<NotificationProviderInstanceDto> listNotificationInstances(ConnectorApiClientDto connector) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r.uri(connector.getUrl() + NOTIFICATION_INSTANCE_BASE_CONTEXT)
                .retrieve()
                .toEntityList(NotificationProviderInstanceDto.class)
                .block()
                .getBody(), request, connector);
    }

    public NotificationProviderInstanceDto getNotificationInstance(ConnectorApiClientDto connector, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r.uri(connector.getUrl() + NOTIFICATION_INSTANCE_IDENTIFIED_CONTEXT, uuid)
                .retrieve()
                .toEntity(NotificationProviderInstanceDto.class)
                .block()
                .getBody(), request, connector);
    }

    public NotificationProviderInstanceDto createNotificationInstance(ConnectorApiClientDto connector, NotificationProviderInstanceRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r.uri(connector.getUrl() + NOTIFICATION_INSTANCE_BASE_CONTEXT)
                .body(Mono.just(requestDto), NotificationProviderInstanceRequestDto.class)
                .retrieve()
                .toEntity(NotificationProviderInstanceDto.class)
                .block()
                .getBody(), request, connector);
    }


    public NotificationProviderInstanceDto updateNotificationInstance(ConnectorApiClientDto connector, String uuid, NotificationProviderInstanceRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.PUT, connector, true);

        return processRequest(r -> r.uri(connector.getUrl() + NOTIFICATION_INSTANCE_IDENTIFIED_CONTEXT, uuid)
                .body(Mono.just(requestDto), NotificationProviderInstanceRequestDto.class)
                .retrieve()
                .toEntity(NotificationProviderInstanceDto.class)
                .block()
                .getBody(), request, connector);
    }

    public void removeNotificationInstance(ConnectorApiClientDto connector, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.DELETE, connector, true);

        processRequest(r -> r.uri(connector.getUrl() + NOTIFICATION_INSTANCE_IDENTIFIED_CONTEXT, uuid)
                .retrieve()
                .toEntity(Void.class)
                .block()
                .getBody(), request, connector);
    }


    public void sendNotification(ConnectorApiClientDto connector, String uuid, NotificationProviderNotifyRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        processRequest(r -> r.uri(connector.getUrl() + NOTIFICATION_INSTANCE_IDENTIFIED_SEND_CONTEXT, uuid)
                .body(Mono.just(requestDto), NotificationProviderNotifyRequestDto.class)
                .retrieve()
                .toEntity(Void.class)
                .block()
                .getBody(), request, connector);
    }

    public List<DataAttribute> listMappingAttributes(ConnectorApiClientDto connector, String kind) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> Objects.requireNonNull(r.uri(connector.getUrl() + NOTIFICATION_INSTANCE_MAPPING_ATTRIBUTES_CONTEXT, kind)
                        .retrieve()
                        .toEntity(new ParameterizedTypeReference<List<DataAttribute>>() {
                        })
                        .block())
                .getBody(), request, connector);
    }


}