package com.czertainly.api.clients;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.common.attribute.v2.BaseAttributeV2;
import com.czertainly.api.model.connector.entity.EntityInstanceDto;
import com.czertainly.api.model.connector.entity.EntityInstanceRequestDto;
import com.czertainly.api.model.core.connector.ConnectorApiClientDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.net.ssl.TrustManager;
import java.util.List;

public class EntityInstanceApiClient extends BaseApiClient {

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

    public List<EntityInstanceDto> listEntityInstances(ConnectorApiClientDto connector) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + ENTITY_INSTANCE_BASE_CONTEXT)
                .retrieve()
                .toEntityList(EntityInstanceDto.class)
                .block().getBody(),
                request,
                connector);
    }

    public EntityInstanceDto getEntityInstance(ConnectorApiClientDto connector, String entityUuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + ENTITY_INSTANCE_IDENTIFIED_CONTEXT, entityUuid)
                .retrieve()
                .toEntity(EntityInstanceDto.class)
                .block().getBody(),
                request,
                connector);
    }

    public EntityInstanceDto createEntityInstance(ConnectorApiClientDto connector, EntityInstanceRequestDto requestDto) throws ConnectorException {
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

    public EntityInstanceDto updateEntityInstance(ConnectorApiClientDto connector, String entityUuid, EntityInstanceRequestDto requestDto) throws ConnectorException {
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

    public void removeEntityInstance(ConnectorApiClientDto connector, String entityUuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.DELETE, connector, true);

        processRequest(r -> r
                .uri(connector.getUrl() + ENTITY_INSTANCE_IDENTIFIED_CONTEXT, entityUuid)
                .retrieve()
                .toEntity(Void.class)
                .block().getBody(),
                request,
                connector);
    }


    public List<BaseAttribute> listLocationAttributes(ConnectorApiClientDto connector, String entityUuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + ENTITY_INSTANCE_LOCATION_ATTRS_CONTEXT, entityUuid)
                .retrieve()
                .toEntityList(BaseAttribute.class)
                .block().getBody(),
                request,
                connector);
    }

    public void validateLocationAttributes(ConnectorApiClientDto connector, String entityUuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException {
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