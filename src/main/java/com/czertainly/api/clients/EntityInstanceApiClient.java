package com.czertainly.api.clients;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.entity.EntityInstanceDto;
import com.czertainly.api.model.connector.entity.EntityInstanceRequestDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import com.czertainly.api.interfaces.client.v1.EntityInstanceSyncApiClient;

import javax.net.ssl.TrustManager;
import java.util.List;

public class EntityInstanceApiClient extends BaseApiClient implements EntityInstanceSyncApiClient {

    private static final String ENTITY_INSTANCE_BASE_CONTEXT = "/v1/entityProvider/entities";
    private static final String ENTITY_INSTANCE_IDENTIFIED_CONTEXT = ENTITY_INSTANCE_BASE_CONTEXT + "/{entityUuid}";
    private static final String ENTITY_INSTANCE_LOCATION_ATTRS_CONTEXT = ENTITY_INSTANCE_IDENTIFIED_CONTEXT + "/location/attributes";
    private static final String ENTITY_INSTANCE_LOCATION_ATTRS_VALIDATE_CONTEXT = ENTITY_INSTANCE_LOCATION_ATTRS_CONTEXT + "/validate";

    private static final ParameterizedTypeReference<List<RequestAttribute>> ATTRIBUTE_LIST_TYPE_REF = new ParameterizedTypeReference<>() {
    };

    public EntityInstanceApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        this.webClient = webClient;
        this.defaultTrustManagers = defaultTrustManagers;
    }

    @Override
    public List<EntityInstanceDto> listEntityInstances(ApiClientConnectorInfo connector) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + ENTITY_INSTANCE_BASE_CONTEXT)
                .retrieve()
                .toEntityList(EntityInstanceDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public EntityInstanceDto getEntityInstance(ApiClientConnectorInfo connector, String entityUuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + ENTITY_INSTANCE_IDENTIFIED_CONTEXT, entityUuid)
                .retrieve()
                .toEntity(EntityInstanceDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public EntityInstanceDto createEntityInstance(ApiClientConnectorInfo connector, EntityInstanceRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + ENTITY_INSTANCE_BASE_CONTEXT)
                .body(Mono.just(requestDto), EntityInstanceRequestDto.class)
                .retrieve()
                .toEntity(EntityInstanceDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public EntityInstanceDto updateEntityInstance(ApiClientConnectorInfo connector, String entityUuid, EntityInstanceRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.PUT, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + ENTITY_INSTANCE_IDENTIFIED_CONTEXT, entityUuid)
                .body(Mono.just(requestDto), EntityInstanceRequestDto.class)
                .retrieve()
                .toEntity(EntityInstanceDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public void removeEntityInstance(ApiClientConnectorInfo connector, String entityUuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.DELETE, connector, true);

        processRequest(r -> r
                .uri(connector.getUrl() + ENTITY_INSTANCE_IDENTIFIED_CONTEXT, entityUuid)
                .retrieve()
                .toEntity(Void.class)
                .block().getBody(),
                request,
                connector);
    }


    @Override
    public List<BaseAttribute> listLocationAttributes(ApiClientConnectorInfo connector, String entityUuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + ENTITY_INSTANCE_LOCATION_ATTRS_CONTEXT, entityUuid)
                .retrieve()
                .toEntityList(BaseAttribute.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public void validateLocationAttributes(ApiClientConnectorInfo connector, String entityUuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        processRequest(r -> r
                .uri(connector.getUrl() + ENTITY_INSTANCE_LOCATION_ATTRS_VALIDATE_CONTEXT, entityUuid)
                .body(Mono.just(attributes), ATTRIBUTE_LIST_TYPE_REF)
                .retrieve()
                .toEntity(Void.class)
                .block().getBody(),
                request,
                connector);
    }
}