package com.czertainly.api.clients;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.authority.AuthorityProviderInstanceDto;
import com.czertainly.api.model.connector.authority.AuthorityProviderInstanceRequestDto;
import com.czertainly.api.model.connector.authority.CertificateRevocationListRequestDto;
import com.czertainly.api.model.connector.authority.CertificateRevocationListResponseDto;
import com.czertainly.api.model.connector.authority.CaCertificatesRequestDto;
import com.czertainly.api.model.connector.authority.CaCertificatesResponseDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import com.czertainly.api.interfaces.client.v1.AuthorityInstanceSyncApiClient;

import javax.net.ssl.TrustManager;
import java.util.List;
import java.util.Objects;

public class AuthorityInstanceApiClient extends BaseApiClient implements AuthorityInstanceSyncApiClient {

    private static final String AUTHORITY_INSTANCE_BASE_CONTEXT = "/v1/authorityProvider/authorities";
    private static final String AUTHORITY_INSTANCE_IDENTIFIED_CONTEXT = AUTHORITY_INSTANCE_BASE_CONTEXT + "/{uuid}";
    private static final String AUTHORITY_INSTANCE_RA_ATTRS_CONTEXT = AUTHORITY_INSTANCE_IDENTIFIED_CONTEXT + "/raProfile/attributes";
    private static final String AUTHORITY_INSTANCE_RA_ATTRS_VALIDATE_CONTEXT = AUTHORITY_INSTANCE_RA_ATTRS_CONTEXT + "/validate";
    private static final String AUTHORITY_INSTANCE_CRL_CONTEXT = AUTHORITY_INSTANCE_IDENTIFIED_CONTEXT + "/crl";
    private static final String AUTHORITY_INSTANCE_CERT_CONTEXT = AUTHORITY_INSTANCE_IDENTIFIED_CONTEXT + "/caCertificates";

    private static final ParameterizedTypeReference<List<RequestAttribute>> ATTRIBUTE_LIST_TYPE_REF = new ParameterizedTypeReference<>() {
    };

    public AuthorityInstanceApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        this.webClient = webClient;
        this.defaultTrustManagers = defaultTrustManagers;
    }

    @Override
    public List<AuthorityProviderInstanceDto> listAuthorityInstances(ApiClientConnectorInfo connector) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + AUTHORITY_INSTANCE_BASE_CONTEXT)
                .retrieve()
                .toEntityList(AuthorityProviderInstanceDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public AuthorityProviderInstanceDto getAuthorityInstance(ApiClientConnectorInfo connector, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + AUTHORITY_INSTANCE_IDENTIFIED_CONTEXT, uuid)
                .retrieve()
                .toEntity(AuthorityProviderInstanceDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public AuthorityProviderInstanceDto createAuthorityInstance(ApiClientConnectorInfo connector, AuthorityProviderInstanceRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + AUTHORITY_INSTANCE_BASE_CONTEXT)
                .body(Mono.just(requestDto), AuthorityProviderInstanceRequestDto.class)
                .retrieve()
                .toEntity(AuthorityProviderInstanceDto.class)
                .block().getBody(),
                request,
                connector);
    }


    @Override
    public AuthorityProviderInstanceDto updateAuthorityInstance(ApiClientConnectorInfo connector, String uuid, AuthorityProviderInstanceRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + AUTHORITY_INSTANCE_IDENTIFIED_CONTEXT, uuid)
                .body(Mono.just(requestDto), AuthorityProviderInstanceRequestDto.class)
                .retrieve()
                .toEntity(AuthorityProviderInstanceDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public void removeAuthorityInstance(ApiClientConnectorInfo connector, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.DELETE, connector, true);

        processRequest(r -> r
                .uri(connector.getUrl() + AUTHORITY_INSTANCE_IDENTIFIED_CONTEXT, uuid)
                .retrieve()
                .toEntity(Void.class)
                .block().getBody(),
                request,
                connector);
    }


    @Override
    public List<BaseAttribute> listRAProfileAttributes(ApiClientConnectorInfo connector, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + AUTHORITY_INSTANCE_RA_ATTRS_CONTEXT, uuid)
                .retrieve()
                .toEntityList(BaseAttribute.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public Boolean validateRAProfileAttributes(ApiClientConnectorInfo connector, String uuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + AUTHORITY_INSTANCE_RA_ATTRS_VALIDATE_CONTEXT, uuid)
                .body(Mono.just(attributes), ATTRIBUTE_LIST_TYPE_REF)
                .retrieve()
                .toEntity(Boolean.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public CertificateRevocationListResponseDto getCrl(ApiClientConnectorInfo connector, String uuid, CertificateRevocationListRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> Objects.requireNonNull(r
                .uri(connector.getUrl() + AUTHORITY_INSTANCE_CRL_CONTEXT, uuid)
                .body(Mono.just(requestDto), CertificateRevocationListRequestDto.class)
                .retrieve()
                .toEntity(CertificateRevocationListResponseDto.class)
                .block()).getBody(),
                request,
                connector);
    }

    @Override
    public CaCertificatesResponseDto getCaCertificates(ApiClientConnectorInfo connector, String uuid, CaCertificatesRequestDto requestDto) throws ValidationException, ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> Objects.requireNonNull(r
                .uri(connector.getUrl() + AUTHORITY_INSTANCE_CERT_CONTEXT, uuid)
                .body(Mono.just(requestDto), CaCertificatesRequestDto.class)
                .retrieve()
                .toEntity(CaCertificatesResponseDto.class)
                .block()).getBody(),
                request,
                connector);
    }
}