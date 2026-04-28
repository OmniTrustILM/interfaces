package com.czertainly.api.clients.cryptography;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.clients.BaseApiClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.interfaces.client.v1.TokenInstanceSyncApiClient;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.cryptography.token.TokenInstanceDto;
import com.czertainly.api.model.connector.cryptography.token.TokenInstanceRequestDto;
import com.czertainly.api.model.connector.cryptography.token.TokenInstanceStatusDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.net.ssl.TrustManager;
import java.util.List;

public class TokenInstanceApiClient extends BaseApiClient implements TokenInstanceSyncApiClient {

    private static final String TOKEN_INSTANCE_BASE_CONTEXT = "/v1/cryptographyProvider/tokens";
    private static final String TOKEN_INSTANCE_IDENTIFIED_CONTEXT = TOKEN_INSTANCE_BASE_CONTEXT + "/{uuid}";
    private static final String TOKEN_INSTANCE_PROFILE_ATTRS_CONTEXT = TOKEN_INSTANCE_IDENTIFIED_CONTEXT + "/tokenProfile/attributes";
    private static final String TOKEN_INSTANCE_PROFILE_ATTRS_VALIDATE_CONTEXT = TOKEN_INSTANCE_PROFILE_ATTRS_CONTEXT + "/validate";
    private static final String TOKEN_INSTANCE_ACTIVATE_ATTRS_CONTEXT = TOKEN_INSTANCE_IDENTIFIED_CONTEXT + "/activate/attributes";
    private static final String TOKEN_INSTANCE_ACTIVATE_ATTRS_VALIDATE_CONTEXT = TOKEN_INSTANCE_ACTIVATE_ATTRS_CONTEXT + "/validate";
    private static final String TOKEN_INSTANCE_ACTIVATE_CONTEXT = TOKEN_INSTANCE_IDENTIFIED_CONTEXT + "/activate";
    private static final String TOKEN_INSTANCE_DEACTIVATE_CONTEXT = TOKEN_INSTANCE_IDENTIFIED_CONTEXT + "/deactivate";
    private static final String TOKEN_INSTANCE_STATUS_CONTEXT = TOKEN_INSTANCE_IDENTIFIED_CONTEXT + "/status";

    private static final ParameterizedTypeReference<List<RequestAttribute>> ATTRIBUTE_LIST_TYPE_REF = new ParameterizedTypeReference<>() {
    };

    public TokenInstanceApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        this.webClient = webClient;
        this.defaultTrustManagers = defaultTrustManagers;
    }

    @Override
    public List<TokenInstanceDto> listTokenInstances(ApiClientConnectorInfo connector) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + TOKEN_INSTANCE_BASE_CONTEXT)
                .retrieve()
                .toEntityList(TokenInstanceDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public TokenInstanceDto getTokenInstance(ApiClientConnectorInfo connector, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + TOKEN_INSTANCE_IDENTIFIED_CONTEXT, uuid)
                .retrieve()
                .toEntity(TokenInstanceDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public TokenInstanceDto createTokenInstance(ApiClientConnectorInfo connector, TokenInstanceRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + TOKEN_INSTANCE_BASE_CONTEXT)
                .body(Mono.just(requestDto), TokenInstanceRequestDto.class)
                .retrieve()
                .toEntity(TokenInstanceDto.class)
                .block().getBody(),
                request,
                connector);
    }


    @Override
    public TokenInstanceDto updateTokenInstance(ApiClientConnectorInfo connector, String uuid, TokenInstanceRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + TOKEN_INSTANCE_IDENTIFIED_CONTEXT, uuid)
                .body(Mono.just(requestDto), TokenInstanceRequestDto.class)
                .retrieve()
                .toEntity(TokenInstanceDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public void removeTokenInstance(ApiClientConnectorInfo connector, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.DELETE, connector, true);

        processRequest(r -> r
                .uri(connector.getUrl() + TOKEN_INSTANCE_IDENTIFIED_CONTEXT, uuid)
                .retrieve()
                .toEntity(Void.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public TokenInstanceStatusDto getTokenInstanceStatus(ApiClientConnectorInfo connector, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + TOKEN_INSTANCE_STATUS_CONTEXT, uuid)
                .retrieve()
                .toEntity(TokenInstanceStatusDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public List<BaseAttribute> listTokenProfileAttributes(ApiClientConnectorInfo connector, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + TOKEN_INSTANCE_PROFILE_ATTRS_CONTEXT, uuid)
                .retrieve()
                .toEntityList(BaseAttribute.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public void validateTokenProfileAttributes(ApiClientConnectorInfo connector, String uuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        processRequest(r -> r
                .uri(connector.getUrl() + TOKEN_INSTANCE_PROFILE_ATTRS_VALIDATE_CONTEXT, uuid)
                .body(Mono.just(attributes), ATTRIBUTE_LIST_TYPE_REF)
                .retrieve()
                .toEntity(Void.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public List<BaseAttribute> listTokenInstanceActivationAttributes(ApiClientConnectorInfo connector, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + TOKEN_INSTANCE_ACTIVATE_ATTRS_CONTEXT, uuid)
                .retrieve()
                .toEntityList(BaseAttribute.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public void validateTokenInstanceActivationAttributes(ApiClientConnectorInfo connector, String uuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        processRequest(r -> r
                .uri(connector.getUrl() + TOKEN_INSTANCE_ACTIVATE_ATTRS_VALIDATE_CONTEXT, uuid)
                .body(Mono.just(attributes), ATTRIBUTE_LIST_TYPE_REF)
                .retrieve()
                .toEntity(Void.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public void activateTokenInstance(ApiClientConnectorInfo connector, String uuid, List<RequestAttribute> attributes) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.PATCH, connector, true);

        processRequest(r -> r
                .uri(connector.getUrl() + TOKEN_INSTANCE_ACTIVATE_CONTEXT, uuid)
                .body(Mono.just(attributes), ATTRIBUTE_LIST_TYPE_REF)
                .retrieve()
                .toEntity(Void.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public void deactivateTokenInstance(ApiClientConnectorInfo connector, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.PATCH, connector, true);

        processRequest(r -> r
                .uri(connector.getUrl() + TOKEN_INSTANCE_DEACTIVATE_CONTEXT, uuid)
                .retrieve()
                .toEntity(Void.class)
                .block().getBody(),
                request,
                connector);
    }

}