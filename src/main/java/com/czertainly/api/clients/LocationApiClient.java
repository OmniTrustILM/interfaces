package com.czertainly.api.clients;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.entity.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import com.czertainly.api.interfaces.client.v1.LocationSyncApiClient;

import javax.net.ssl.TrustManager;
import java.util.List;

public class LocationApiClient extends BaseApiClient implements LocationSyncApiClient {

    private static final String LOCATION_BASE_CONTEXT = "/v1/entityProvider/entities/{entityUuid}/locations";
    private static final String LOCATION_PUSH_CONTEXT = LOCATION_BASE_CONTEXT + "/push";
    private static final String LOCATION_PUSH_ATTRS_CONTEXT = LOCATION_BASE_CONTEXT + "/push/attributes";
    private static final String LOCATION_PUSH_ATTRS_VALIDATE_CONTEXT = LOCATION_BASE_CONTEXT + "/push/attributes/validate";
    private static final String LOCATION_REMOVE_CONTEXT = LOCATION_BASE_CONTEXT + "/remove";
    private static final String LOCATION_CSR_CONTEXT = LOCATION_BASE_CONTEXT + "/csr";
    private static final String LOCATION_CSR_ATTRS_CONTEXT = LOCATION_BASE_CONTEXT + "/csr/attributes";
    private static final String LOCATION_CSR_ATTRS_VALIDATE_CONTEXT = LOCATION_BASE_CONTEXT + "/csr/attributes/validate";

    private static final ParameterizedTypeReference<List<RequestAttribute>> ATTRIBUTE_LIST_TYPE_REF = new ParameterizedTypeReference<>() {
    };

    public LocationApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        this.webClient = webClient;
        this.defaultTrustManagers = defaultTrustManagers;
    }

    @Override
    public LocationDetailResponseDto getLocationDetail(ApiClientConnectorInfo connector, String entityUuid, LocationDetailRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + LOCATION_BASE_CONTEXT, entityUuid)
                .body(Mono.just(requestDto), LocationDetailRequestDto.class)
                .retrieve()
                .toEntity(LocationDetailResponseDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public PushCertificateResponseDto pushCertificateToLocation(ApiClientConnectorInfo connector, String entityUuid, PushCertificateRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + LOCATION_PUSH_CONTEXT, entityUuid)
                .body(Mono.just(requestDto), PushCertificateRequestDto.class)
                .retrieve()
                .toEntity(PushCertificateResponseDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public List<BaseAttribute> listPushCertificateAttributes(ApiClientConnectorInfo connector, String entityUuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + LOCATION_PUSH_ATTRS_CONTEXT, entityUuid)
                .retrieve()
                .toEntityList(BaseAttribute.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public void validatePushCertificateAttributes(ApiClientConnectorInfo connector, String entityUuid, List<RequestAttribute> pushAttributes) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        processRequest(r -> r
                .uri(connector.getUrl() + LOCATION_PUSH_ATTRS_VALIDATE_CONTEXT, entityUuid)
                .body(Mono.just(pushAttributes), ATTRIBUTE_LIST_TYPE_REF)
                .retrieve()
                .toEntity(Void.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public RemoveCertificateResponseDto removeCertificateFromLocation(ApiClientConnectorInfo connector, String entityUuid, RemoveCertificateRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + LOCATION_REMOVE_CONTEXT, entityUuid)
                .body(Mono.just(requestDto), RemoveCertificateRequestDto.class)
                .retrieve()
                .toEntity(RemoveCertificateResponseDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public GenerateCsrResponseDto generateCsrLocation(ApiClientConnectorInfo connector, String entityUuid, GenerateCsrRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + LOCATION_CSR_CONTEXT, entityUuid)
                .body(Mono.just(requestDto), GenerateCsrRequestDto.class)
                .retrieve()
                .toEntity(GenerateCsrResponseDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public List<BaseAttribute> listGenerateCsrAttributes(ApiClientConnectorInfo connector, String entityUuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + LOCATION_CSR_ATTRS_CONTEXT, entityUuid)
                .retrieve()
                .toEntityList(BaseAttribute.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public void validateGenerateCsrAttributes(ApiClientConnectorInfo connector, String entityUuid, List<RequestAttribute> pushAttributes) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        processRequest(r -> r
                .uri(connector.getUrl() + LOCATION_CSR_ATTRS_VALIDATE_CONTEXT, entityUuid)
                .body(Mono.just(pushAttributes), ATTRIBUTE_LIST_TYPE_REF)
                .retrieve()
                .toEntity(Void.class)
                .block().getBody(),
                request,
                connector);
    }

}