package com.czertainly.api.clients.cryptography;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.clients.BaseApiClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.interfaces.client.v1.CryptographicOperationsSyncApiClient;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.cryptography.operations.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.net.ssl.TrustManager;
import java.util.List;

public class CryptographicOperationsApiClient extends BaseApiClient implements CryptographicOperationsSyncApiClient {

    private static final String CRYPTOP_BASE_CONTEXT = "/v1/cryptographyProvider/tokens/{uuid}/keys";
    private static final String CRYPTOP_ENCRYPT_CONTEXT = CRYPTOP_BASE_CONTEXT + "/{keyUuid}/encrypt";
    private static final String CRYPTOP_DECRYPT_CONTEXT = CRYPTOP_BASE_CONTEXT + "/{keyUuid}/decrypt";
    private static final String CRYPTOP_SIGN_CONTEXT = CRYPTOP_BASE_CONTEXT + "/{keyUuid}/sign";
    private static final String CRYPTOP_VERIFY_CONTEXT = CRYPTOP_BASE_CONTEXT + "/{keyUuid}/verify";
    private static final String CRYPTOP_RANDOM_CONTEXT = CRYPTOP_BASE_CONTEXT + "/random";
    private static final String CRYPTOP_RANDOM_ATTRS_CONTEXT = CRYPTOP_RANDOM_CONTEXT + "/attributes";
    private static final String CRYPTOP_RANDOM_ATTRS_VALIDATE_CONTEXT = CRYPTOP_RANDOM_ATTRS_CONTEXT + "/validate";

    private static final ParameterizedTypeReference<List<RequestAttribute>> ATTRIBUTE_LIST_TYPE_REF = new ParameterizedTypeReference<>() {
    };

    public CryptographicOperationsApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        this.webClient = webClient;
        this.defaultTrustManagers = defaultTrustManagers;
    }

    @Override
    public EncryptDataResponseDto encryptData(ApiClientConnectorInfo connector, String uuid, String keyUuid, CipherDataRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + CRYPTOP_ENCRYPT_CONTEXT, uuid, keyUuid)
                .body(Mono.just(requestDto), CipherDataRequestDto.class)
                .retrieve()
                .toEntity(EncryptDataResponseDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public DecryptDataResponseDto decryptData(ApiClientConnectorInfo connector, String uuid, String keyUuid, CipherDataRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + CRYPTOP_DECRYPT_CONTEXT, uuid, keyUuid)
                .body(Mono.just(requestDto), CipherDataRequestDto.class)
                .retrieve()
                .toEntity(DecryptDataResponseDto.class)
                .block().getBody(),
                request,
                connector);
    }


    @Override
    public SignDataResponseDto signData(ApiClientConnectorInfo connector, String uuid, String keyUuid, SignDataRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + CRYPTOP_SIGN_CONTEXT, uuid, keyUuid)
                .body(Mono.just(requestDto), SignDataRequestDto.class)
                .retrieve()
                .toEntity(SignDataResponseDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public VerifyDataResponseDto verifyData(ApiClientConnectorInfo connector, String uuid, String keyUuid, VerifyDataRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + CRYPTOP_VERIFY_CONTEXT, uuid, keyUuid)
                .body(Mono.just(requestDto), VerifyDataRequestDto.class)
                .retrieve()
                .toEntity(VerifyDataResponseDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public List<BaseAttribute> listRandomAttributes(ApiClientConnectorInfo connector, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + CRYPTOP_RANDOM_ATTRS_CONTEXT, uuid)
                .retrieve()
                .toEntityList(BaseAttribute.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public void validateRandomAttributes(ApiClientConnectorInfo connector, String uuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        processRequest(r -> r
                .uri(connector.getUrl() + CRYPTOP_RANDOM_ATTRS_VALIDATE_CONTEXT, uuid)
                .body(Mono.just(attributes), ATTRIBUTE_LIST_TYPE_REF)
                .retrieve()
                .toEntity(Void.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public RandomDataResponseDto randomData(ApiClientConnectorInfo connector, String uuid, RandomDataRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + CRYPTOP_RANDOM_CONTEXT, uuid)
                .body(Mono.just(requestDto), RandomDataRequestDto.class)
                .retrieve()
                .toEntity(RandomDataResponseDto.class)
                .block().getBody(),
                request,
                connector);
    }

}