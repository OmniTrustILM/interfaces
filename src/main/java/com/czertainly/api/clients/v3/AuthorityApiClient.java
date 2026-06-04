package com.czertainly.api.clients.v3;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.clients.BaseApiClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.interfaces.client.v3.AuthoritySyncApiClient;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.v3.authority.CaCertificatesRequestDtoV3;
import com.czertainly.api.model.connector.v3.authority.CaCertificatesResponseDto;
import com.czertainly.api.model.connector.v3.authority.CrlRequestDtoV3;
import com.czertainly.api.model.connector.v3.authority.CrlResponseDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.net.ssl.TrustManager;
import java.util.List;

/**
 * WebClient (HTTP) implementation of v3 Authority API client.
 *
 * <p>Path constants are part of the v3 connector API contract — they describe the routes
 * a v3 connector implementation must expose, not configurable URIs.
 * Mirror of {@link com.czertainly.api.clients.mq.v3.AuthorityApiClient}.</p>
 *
 * <p>v3 is stateless: no authorityUuid path parameter — authority identity travels in the
 * request DTO's authorityAttributes. checkAuthorityConnection takes a List&lt;RequestAttribute&gt;
 * directly since it validates attributes before any authority exists.</p>
 */
@SuppressWarnings("java:S1075") // contract paths, not configurable URIs
public class AuthorityApiClient extends BaseApiClient implements AuthoritySyncApiClient {

    private static final String AUTHORITY_BASE_CONTEXT = "/v3/authorityProvider/authorities";
    private static final String AUTHORITY_ATTRIBUTES_CONTEXT = AUTHORITY_BASE_CONTEXT + "/attributes";
    private static final String AUTHORITY_RA_PROFILE_ATTRIBUTES_CONTEXT = AUTHORITY_BASE_CONTEXT + "/raProfile/attributes";
    private static final String AUTHORITY_CRL_CONTEXT = AUTHORITY_BASE_CONTEXT + "/crl";
    private static final String AUTHORITY_CA_CERTIFICATES_CONTEXT = AUTHORITY_BASE_CONTEXT + "/caCertificates";

    private static final ParameterizedTypeReference<List<RequestAttribute>> ATTRIBUTE_LIST_TYPE_REF = new ParameterizedTypeReference<>() {
    };

    public AuthorityApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        this.webClient = webClient;
        this.defaultTrustManagers = defaultTrustManagers;
    }

    @Override
    public List<BaseAttribute> listAuthorityAttributes(ApiClientConnectorInfo connector) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> requireBody(r
                        .uri(connector.getUrl() + AUTHORITY_ATTRIBUTES_CONTEXT)
                        .retrieve()
                        .toEntityList(BaseAttribute.class), "listAuthorityAttributes"),
                request,
                connector);
    }

    @Override
    public ResponseEntity<Void> checkAuthorityConnection(ApiClientConnectorInfo connector, List<RequestAttribute> authorityAttributes) throws ValidationException, ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> requireResponse(r
                        .uri(connector.getUrl() + AUTHORITY_BASE_CONTEXT)
                        .body(Mono.just(authorityAttributes), ATTRIBUTE_LIST_TYPE_REF)
                        .retrieve()
                        .toBodilessEntity(), "checkAuthorityConnection"),
                request,
                connector);
    }

    @Override
    public List<BaseAttribute> listRaProfileAttributes(ApiClientConnectorInfo connector, List<RequestAttribute> authorityAttributes) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> requireBody(r
                        .uri(connector.getUrl() + AUTHORITY_RA_PROFILE_ATTRIBUTES_CONTEXT)
                        .body(Mono.just(authorityAttributes), ATTRIBUTE_LIST_TYPE_REF)
                        .retrieve()
                        .toEntityList(BaseAttribute.class), "listRaProfileAttributes"),
                request,
                connector);
    }

    @Override
    public CrlResponseDto getCrl(ApiClientConnectorInfo connector, CrlRequestDtoV3 requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> requireBody(r
                        .uri(connector.getUrl() + AUTHORITY_CRL_CONTEXT)
                        .body(Mono.just(requestDto), CrlRequestDtoV3.class)
                        .retrieve()
                        .toEntity(CrlResponseDto.class), "getCrl"),
                request,
                connector);
    }

    @Override
    public CaCertificatesResponseDto getCaCertificates(ApiClientConnectorInfo connector, CaCertificatesRequestDtoV3 requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> requireBody(r
                        .uri(connector.getUrl() + AUTHORITY_CA_CERTIFICATES_CONTEXT)
                        .body(Mono.just(requestDto), CaCertificatesRequestDtoV3.class)
                        .retrieve()
                        .toEntity(CaCertificatesResponseDto.class), "getCaCertificates"),
                request,
                connector);
    }
}
