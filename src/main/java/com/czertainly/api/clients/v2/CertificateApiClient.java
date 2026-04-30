package com.czertainly.api.clients.v2;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.clients.BaseApiClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.interfaces.client.v2.CertificateSyncApiClient;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.v2.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.net.ssl.TrustManager;
import java.util.List;

public class CertificateApiClient extends BaseApiClient implements CertificateSyncApiClient {

    private static final String CERTIFICATE_BASE_CONTEXT = "/v2/authorityProvider/authorities/{uuid}/certificates";

    private static final String CERTIFICATE_ISSUE_CONTEXT = CERTIFICATE_BASE_CONTEXT + "/issue";
    private static final String CERTIFICATE_ISSUE_ATTRIBUTES_CONTEXT = CERTIFICATE_ISSUE_CONTEXT + "/attributes";
    private static final String CERTIFICATE_ISSUE_ATTRIBUTES_VALIDATE_CONTEXT = CERTIFICATE_ISSUE_ATTRIBUTES_CONTEXT + "/validate";

    private static final String CERTIFICATE_RENEW_CONTEXT = CERTIFICATE_BASE_CONTEXT + "/renew";

    private static final String CERTIFICATE_REVOKE_ATTRIBUTES_CONTEXT = CERTIFICATE_BASE_CONTEXT + "/revoke/attributes";
    private static final String CERTIFICATE_REVOKE_ATTRIBUTES_VALIDATE_CONTEXT = CERTIFICATE_REVOKE_ATTRIBUTES_CONTEXT + "/validate";

    private static final String CERTIFICATE_REVOKE_CONTEXT = CERTIFICATE_BASE_CONTEXT + "/revoke";
    private static final String CERTIFICATE_IDENTIFY_CONTEXT = CERTIFICATE_BASE_CONTEXT + "/identify";

    private static final ParameterizedTypeReference<List<RequestAttribute>> ATTRIBUTE_LIST_TYPE_REF = new ParameterizedTypeReference<>() {
    };

    public CertificateApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        this.webClient = webClient;
        this.defaultTrustManagers = defaultTrustManagers;
    }

    @Override
    public List<BaseAttribute> listIssueCertificateAttributes(ApiClientConnectorInfo connector, String authorityUuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + CERTIFICATE_ISSUE_ATTRIBUTES_CONTEXT, authorityUuid)
                .retrieve()
                .toEntityList(BaseAttribute.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public Boolean validateIssueCertificateAttributes(ApiClientConnectorInfo connector, String authorityUuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + CERTIFICATE_ISSUE_ATTRIBUTES_VALIDATE_CONTEXT, authorityUuid)
                .body(Mono.just(attributes), ATTRIBUTE_LIST_TYPE_REF)
                .retrieve()
                .toEntity(Boolean.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public CertificateDataResponseDto issueCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertificateSignRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + CERTIFICATE_ISSUE_CONTEXT, authorityUuid)
                .body(Mono.just(requestDto), CertificateSignRequestDto.class)
                .retrieve()
                .toEntity(CertificateDataResponseDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public CertificateDataResponseDto renewCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertificateRenewRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + CERTIFICATE_RENEW_CONTEXT, authorityUuid)
                .body(Mono.just(requestDto), CertificateRenewRequestDto.class)
                .retrieve()
                .toEntity(CertificateDataResponseDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public List<BaseAttribute> listRevokeCertificateAttributes(ApiClientConnectorInfo connector, String authorityUuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + CERTIFICATE_REVOKE_ATTRIBUTES_CONTEXT, authorityUuid)
                .retrieve()
                .toEntityList(BaseAttribute.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public Boolean validateRevokeCertificateAttributes(ApiClientConnectorInfo connector, String authorityUuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + CERTIFICATE_REVOKE_ATTRIBUTES_VALIDATE_CONTEXT, authorityUuid)
                .body(Mono.just(attributes), ATTRIBUTE_LIST_TYPE_REF)
                .retrieve()
                .toEntity(Boolean.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public void revokeCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertRevocationDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        processRequest(r -> r
                .uri(connector.getUrl() + CERTIFICATE_REVOKE_CONTEXT, authorityUuid)
                .body(Mono.just(requestDto), CertRevocationDto.class)
                .retrieve()
                .toEntity(Void.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public CertificateIdentificationResponseDto identifyCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertificateIdentificationRequestDto requestDto) throws ValidationException, ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                        .uri(connector.getUrl() + CERTIFICATE_IDENTIFY_CONTEXT, authorityUuid)
                        .body(Mono.just(requestDto), CertificateSignRequestDto.class)
                        .retrieve()
                        .toEntity(CertificateIdentificationResponseDto.class)
                        .block().getBody(),
                request,
                connector);
    }
}
