package com.czertainly.api.clients.cryptography;

import com.czertainly.api.clients.BaseApiClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.cryptography.token.TokenInstanceDto;
import com.czertainly.api.model.connector.cryptography.token.TokenInstanceRequestDto;
import com.czertainly.api.model.connector.cryptography.token.TokenInstanceStatusDto;
import com.czertainly.api.model.core.connector.ConnectorApiClientDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.net.ssl.TrustManager;
import java.util.List;

public class TokenInstanceApiClient extends BaseApiClient {

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

    public List<TokenInstanceDto> listTokenInstances(ConnectorApiClientDto connector) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + TOKEN_INSTANCE_BASE_CONTEXT)
                .retrieve()
                .toEntityList(TokenInstanceDto.class)
                .block().getBody(),
                request,
                connector);
    }

    public TokenInstanceDto getTokenInstance(ConnectorApiClientDto connector, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + TOKEN_INSTANCE_IDENTIFIED_CONTEXT, uuid)
                .retrieve()
                .toEntity(TokenInstanceDto.class)
                .block().getBody(),
                request,
                connector);
    }

    public TokenInstanceDto createTokenInstance(ConnectorApiClientDto connector, TokenInstanceRequestDto requestDto) throws ConnectorException {
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


    public TokenInstanceDto updateTokenInstance(ConnectorApiClientDto connector, String uuid, TokenInstanceRequestDto requestDto) throws ConnectorException {
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

    public void removeTokenInstance(ConnectorApiClientDto connector, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.DELETE, connector, true);

        processRequest(r -> r
                .uri(connector.getUrl() + TOKEN_INSTANCE_IDENTIFIED_CONTEXT, uuid)
                .retrieve()
                .toEntity(Void.class)
                .block().getBody(),
                request,
                connector);
    }

    public TokenInstanceStatusDto getTokenInstanceStatus(ConnectorApiClientDto connector, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + TOKEN_INSTANCE_STATUS_CONTEXT, uuid)
                .retrieve()
                .toEntity(TokenInstanceStatusDto.class)
                .block().getBody(),
                request,
                connector);
    }

    public List<BaseAttribute> listTokenProfileAttributes(ConnectorApiClientDto connector, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + TOKEN_INSTANCE_PROFILE_ATTRS_CONTEXT, uuid)
                .retrieve()
                .toEntityList(BaseAttribute.class)
                .block().getBody(),
                request,
                connector);
    }

    public void validateTokenProfileAttributes(ConnectorApiClientDto connector, String uuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException {
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

    public List<BaseAttribute> listTokenInstanceActivationAttributes(ConnectorApiClientDto connector, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + TOKEN_INSTANCE_ACTIVATE_ATTRS_CONTEXT, uuid)
                .retrieve()
                .toEntityList(BaseAttribute.class)
                .block().getBody(),
                request,
                connector);
    }

    public void validateTokenInstanceActivationAttributes(ConnectorApiClientDto connector, String uuid, List<RequestAttribute>attributes) throws ValidationException, ConnectorException {
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

    public void activateTokenInstance(ConnectorApiClientDto connector, String uuid, List<RequestAttribute>attributes) throws ConnectorException {
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

    public void deactivateTokenInstance(ConnectorApiClientDto connector, String uuid) throws ConnectorException {
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