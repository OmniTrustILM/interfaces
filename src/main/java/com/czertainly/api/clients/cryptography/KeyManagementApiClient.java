package com.czertainly.api.clients.cryptography;

import com.czertainly.api.clients.BaseApiClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.cryptography.key.CreateKeyRequestDto;
import com.czertainly.api.model.connector.cryptography.key.KeyDataResponseDto;
import com.czertainly.api.model.connector.cryptography.key.KeyPairDataResponseDto;
import com.czertainly.api.model.core.connector.ConnectorApiClientDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.net.ssl.TrustManager;
import java.util.List;

public class KeyManagementApiClient extends BaseApiClient {

    private static final String KEY_BASE_CONTEXT = "/v1/cryptographyProvider/tokens/{uuid}/keys";
    private static final String KEY_CREATE_SECRET_KEY_CONTEXT = KEY_BASE_CONTEXT + "/secret";
    private static final String KEY_CREATE_SECRET_KEY_ATTRIBUTES_CONTEXT = KEY_CREATE_SECRET_KEY_CONTEXT + "/attributes";
    private static final String KEY_CREATE_SECRET_KEY_ATTRIBUTES_VALIDATE_CONTEXT = KEY_CREATE_SECRET_KEY_ATTRIBUTES_CONTEXT + "/validate";
    private static final String KEY_CREATE_KEY_PAIR_CONTEXT = KEY_BASE_CONTEXT + "/pair";
    private static final String KEY_CREATE_KEY_PAIR_ATTRIBUTES_CONTEXT = KEY_CREATE_KEY_PAIR_CONTEXT + "/attributes";
    private static final String KEY_CREATE_KEY_PAIR_ATTRIBUTES_VALIDATE_CONTEXT = KEY_CREATE_KEY_PAIR_ATTRIBUTES_CONTEXT + "/validate";
    private static final String KEY_LIST_CONTEXT = KEY_BASE_CONTEXT;
    private static final String KEY_DETAILS_CONTEXT = KEY_BASE_CONTEXT + "/{keyUuid}";

    private static final ParameterizedTypeReference<List<RequestAttribute>> ATTRIBUTE_LIST_TYPE_REF = new ParameterizedTypeReference<>() {
    };

    public KeyManagementApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        this.webClient = webClient;
        this.defaultTrustManagers = defaultTrustManagers;
    }

    public List<BaseAttribute> listCreateSecretKeyAttributes(ConnectorApiClientDto connector, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + KEY_CREATE_SECRET_KEY_ATTRIBUTES_CONTEXT, uuid)
                .retrieve()
                .toEntityList(BaseAttribute.class)
                .block().getBody(),
                request,
                connector);
    }

    public void validateCreateSecretKeyAttributes(ConnectorApiClientDto connector, String uuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        processRequest(r -> r
                .uri(connector.getUrl() + KEY_CREATE_SECRET_KEY_ATTRIBUTES_VALIDATE_CONTEXT, uuid)
                .body(Mono.just(attributes), ATTRIBUTE_LIST_TYPE_REF)
                .retrieve()
                .toEntity(Void.class)
                .block().getBody(),
                request,
                connector);
    }

    public KeyDataResponseDto createSecretKey(ConnectorApiClientDto connector, String uuid, CreateKeyRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + KEY_CREATE_SECRET_KEY_CONTEXT, uuid)
                .body(Mono.just(requestDto), CreateKeyRequestDto.class)
                .retrieve()
                .toEntity(KeyDataResponseDto.class)
                .block().getBody(),
                request,
                connector);
    }

    public List<BaseAttribute> listCreateKeyPairAttributes(ConnectorApiClientDto connector, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                        .uri(connector.getUrl() + KEY_CREATE_KEY_PAIR_ATTRIBUTES_CONTEXT, uuid)
                        .retrieve()
                        .toEntityList(BaseAttribute.class)
                        .block().getBody(),
                request,
                connector);
    }

    public void validateCreateKeyPairAttributes(ConnectorApiClientDto connector, String uuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        processRequest(r -> r
                        .uri(connector.getUrl() + KEY_CREATE_KEY_PAIR_ATTRIBUTES_VALIDATE_CONTEXT, uuid)
                        .body(Mono.just(attributes), ATTRIBUTE_LIST_TYPE_REF)
                        .retrieve()
                        .toEntity(Void.class)
                        .block().getBody(),
                request,
                connector);
    }

    public KeyPairDataResponseDto createKeyPair(ConnectorApiClientDto connector, String uuid, CreateKeyRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + KEY_CREATE_KEY_PAIR_CONTEXT, uuid)
                .body(Mono.just(requestDto), CreateKeyRequestDto.class)
                .retrieve()
                .toEntity(KeyPairDataResponseDto.class)
                .block().getBody(),
                request,
                connector);
    }

    public List<KeyDataResponseDto> listKeys(ConnectorApiClientDto connector, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + KEY_LIST_CONTEXT, uuid)
                .retrieve()
                .toEntityList(KeyDataResponseDto.class)
                .block().getBody(),
                request,
                connector);
    }

    public KeyDataResponseDto getKey(ConnectorApiClientDto connector, String uuid, String keyUuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + KEY_DETAILS_CONTEXT, uuid, keyUuid)
                .retrieve()
                .toEntity(KeyDataResponseDto.class)
                .block().getBody(),
                request,
                connector);
    }

    public void destroyKey(ConnectorApiClientDto connector, String uuid, String keyUuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.DELETE, connector, true);

        processRequest(r -> r
                        .uri(connector.getUrl() + KEY_DETAILS_CONTEXT, uuid, keyUuid)
                        .retrieve()
                        .toEntity(Void.class)
                        .block().getBody(),
                request,
                connector);
    }

}
