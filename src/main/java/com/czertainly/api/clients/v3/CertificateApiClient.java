package com.czertainly.api.clients.v3;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.clients.BaseApiClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v3.CertificateSyncApiClient;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.v3.certificate.CertificateDataResponseDto;
import com.czertainly.api.model.connector.v3.certificate.CertificateIdentificationRequestDto;
import com.czertainly.api.model.connector.v3.certificate.CertificateIdentificationResponseDto;
import com.czertainly.api.model.connector.v3.certificate.CertificateOperationCancelRequestDto;
import com.czertainly.api.model.connector.v3.certificate.CertificateOperationStatusRequestDto;
import com.czertainly.api.model.connector.v3.certificate.CertificateOperationStatusResponseDto;
import com.czertainly.api.model.connector.v3.certificate.CertificateRegistrationRequestDto;
import com.czertainly.api.model.connector.v3.certificate.CertificateRenewRequestDto;
import com.czertainly.api.model.connector.v3.certificate.CertificateRevocationRequestDto;
import com.czertainly.api.model.connector.v3.certificate.CertificateSignRequestDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.net.ssl.TrustManager;
import java.util.List;

/**
 * WebClient (HTTP) implementation of v3 Certificate API client. Mirror of
 * {@link com.czertainly.api.clients.mq.v3.CertificateApiClient}.
 *
 * <p>Response semantics by operation:</p>
 * <ul>
 *   <li><b>issue / renew / register:</b> 200 OK = sync result (cert in body); 202 Accepted = async accepted (meta as tracking handle).</li>
 *   <li><b>revoke:</b> 204 No Content = sync success; 202 Accepted = async accepted with meta.</li>
 *   <li><b>cancel endpoints:</b> 204 No Content = success; 404 / 422 surface via {@link com.czertainly.api.exception.ConnectorProblemException}.</li>
 *   <li><b>identify / getCrl / getCaCertificates / status / attribute list:</b> 200 OK with body; always synchronous.</li>
 * </ul>
 */
@SuppressWarnings("java:S1075") // contract paths, not configurable URIs
public class CertificateApiClient extends BaseApiClient implements CertificateSyncApiClient {

    private static final String CERTIFICATE_BASE_CONTEXT = "/v3/authorityProvider/certificates";

    private static final String CERTIFICATE_ISSUE_ATTRIBUTES_CONTEXT = CERTIFICATE_BASE_CONTEXT + "/issue/attributes";
    private static final String CERTIFICATE_ISSUE_CONTEXT = CERTIFICATE_BASE_CONTEXT + "/issue";
    private static final String CERTIFICATE_ISSUE_STATUS_CONTEXT = CERTIFICATE_BASE_CONTEXT + "/issue/status";
    private static final String CERTIFICATE_ISSUE_CANCEL_CONTEXT = CERTIFICATE_BASE_CONTEXT + "/issue/cancel";

    private static final String CERTIFICATE_RENEW_CONTEXT = CERTIFICATE_BASE_CONTEXT + "/renew";

    private static final String CERTIFICATE_REVOKE_ATTRIBUTES_CONTEXT = CERTIFICATE_BASE_CONTEXT + "/revoke/attributes";
    private static final String CERTIFICATE_REVOKE_CONTEXT = CERTIFICATE_BASE_CONTEXT + "/revoke";
    private static final String CERTIFICATE_REVOKE_STATUS_CONTEXT = CERTIFICATE_BASE_CONTEXT + "/revoke/status";
    private static final String CERTIFICATE_REVOKE_CANCEL_CONTEXT = CERTIFICATE_BASE_CONTEXT + "/revoke/cancel";

    private static final String CERTIFICATE_REGISTER_ATTRIBUTES_CONTEXT = CERTIFICATE_BASE_CONTEXT + "/register/attributes";
    private static final String CERTIFICATE_REGISTER_CONTEXT = CERTIFICATE_BASE_CONTEXT + "/register";
    private static final String CERTIFICATE_REGISTER_STATUS_CONTEXT = CERTIFICATE_BASE_CONTEXT + "/register/status";
    private static final String CERTIFICATE_REGISTER_CANCEL_CONTEXT = CERTIFICATE_BASE_CONTEXT + "/register/cancel";

    private static final String CERTIFICATE_IDENTIFY_CONTEXT = CERTIFICATE_BASE_CONTEXT + "/identify";

    private static final ParameterizedTypeReference<List<RequestAttribute>> ATTRIBUTE_LIST_TYPE_REF = new ParameterizedTypeReference<>() {
    };

    public CertificateApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        this.webClient = webClient;
        this.defaultTrustManagers = defaultTrustManagers;
    }

    // ---- Issue ----

    @Override
    public List<BaseAttribute> listIssueAttributes(ApiClientConnectorInfo connector, List<RequestAttribute> authorityAttributes) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> requireBody(r
                        .uri(connector.getUrl() + CERTIFICATE_ISSUE_ATTRIBUTES_CONTEXT)
                        .body(Mono.just(authorityAttributes), ATTRIBUTE_LIST_TYPE_REF)
                        .retrieve()
                        .toEntityList(BaseAttribute.class), "listIssueAttributes"),
                request,
                connector);
    }

    @Override
    public ResponseEntity<CertificateDataResponseDto> issue(ApiClientConnectorInfo connector, CertificateSignRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> requireResponse(r
                        .uri(connector.getUrl() + CERTIFICATE_ISSUE_CONTEXT)
                        .body(Mono.just(requestDto), CertificateSignRequestDto.class)
                        .retrieve()
                        .toEntity(CertificateDataResponseDto.class), "issue"),
                request,
                connector);
    }

    @Override
    public CertificateOperationStatusResponseDto getIssueStatus(ApiClientConnectorInfo connector, CertificateOperationStatusRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> requireBody(r
                        .uri(connector.getUrl() + CERTIFICATE_ISSUE_STATUS_CONTEXT)
                        .body(Mono.just(requestDto), CertificateOperationStatusRequestDto.class)
                        .retrieve()
                        .toEntity(CertificateOperationStatusResponseDto.class), "getIssueStatus"),
                request,
                connector);
    }

    @Override
    public ResponseEntity<Void> cancelIssue(ApiClientConnectorInfo connector, CertificateOperationCancelRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> requireResponse(r
                        .uri(connector.getUrl() + CERTIFICATE_ISSUE_CANCEL_CONTEXT)
                        .body(Mono.just(requestDto), CertificateOperationCancelRequestDto.class)
                        .retrieve()
                        .toBodilessEntity(), "cancelIssue"),
                request,
                connector);
    }

    // ---- Renew (status/cancel via /issue/*) ----

    @Override
    public ResponseEntity<CertificateDataResponseDto> renew(ApiClientConnectorInfo connector, CertificateRenewRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> requireResponse(r
                        .uri(connector.getUrl() + CERTIFICATE_RENEW_CONTEXT)
                        .body(Mono.just(requestDto), CertificateRenewRequestDto.class)
                        .retrieve()
                        .toEntity(CertificateDataResponseDto.class), "renew"),
                request,
                connector);
    }

    // ---- Revoke ----

    @Override
    public List<BaseAttribute> listRevokeAttributes(ApiClientConnectorInfo connector, List<RequestAttribute> authorityAttributes) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> requireBody(r
                        .uri(connector.getUrl() + CERTIFICATE_REVOKE_ATTRIBUTES_CONTEXT)
                        .body(Mono.just(authorityAttributes), ATTRIBUTE_LIST_TYPE_REF)
                        .retrieve()
                        .toEntityList(BaseAttribute.class), "listRevokeAttributes"),
                request,
                connector);
    }

    @Override
    public ResponseEntity<CertificateDataResponseDto> revoke(ApiClientConnectorInfo connector, CertificateRevocationRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> requireResponse(r
                        .uri(connector.getUrl() + CERTIFICATE_REVOKE_CONTEXT)
                        .body(Mono.just(requestDto), CertificateRevocationRequestDto.class)
                        .retrieve()
                        .toEntity(CertificateDataResponseDto.class), "revoke"),
                request,
                connector);
    }

    @Override
    public CertificateOperationStatusResponseDto getRevokeStatus(ApiClientConnectorInfo connector, CertificateOperationStatusRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> requireBody(r
                        .uri(connector.getUrl() + CERTIFICATE_REVOKE_STATUS_CONTEXT)
                        .body(Mono.just(requestDto), CertificateOperationStatusRequestDto.class)
                        .retrieve()
                        .toEntity(CertificateOperationStatusResponseDto.class), "getRevokeStatus"),
                request,
                connector);
    }

    @Override
    public ResponseEntity<Void> cancelRevoke(ApiClientConnectorInfo connector, CertificateOperationCancelRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> requireResponse(r
                        .uri(connector.getUrl() + CERTIFICATE_REVOKE_CANCEL_CONTEXT)
                        .body(Mono.just(requestDto), CertificateOperationCancelRequestDto.class)
                        .retrieve()
                        .toBodilessEntity(), "cancelRevoke"),
                request,
                connector);
    }

    // ---- Register ----

    @Override
    public List<BaseAttribute> listRegisterAttributes(ApiClientConnectorInfo connector, List<RequestAttribute> authorityAttributes) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> requireBody(r
                        .uri(connector.getUrl() + CERTIFICATE_REGISTER_ATTRIBUTES_CONTEXT)
                        .body(Mono.just(authorityAttributes), ATTRIBUTE_LIST_TYPE_REF)
                        .retrieve()
                        .toEntityList(BaseAttribute.class), "listRegisterAttributes"),
                request,
                connector);
    }

    @Override
    public ResponseEntity<CertificateDataResponseDto> register(ApiClientConnectorInfo connector, CertificateRegistrationRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> requireResponse(r
                        .uri(connector.getUrl() + CERTIFICATE_REGISTER_CONTEXT)
                        .body(Mono.just(requestDto), CertificateRegistrationRequestDto.class)
                        .retrieve()
                        .toEntity(CertificateDataResponseDto.class), "register"),
                request,
                connector);
    }

    @Override
    public CertificateOperationStatusResponseDto getRegisterStatus(ApiClientConnectorInfo connector, CertificateOperationStatusRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> requireBody(r
                        .uri(connector.getUrl() + CERTIFICATE_REGISTER_STATUS_CONTEXT)
                        .body(Mono.just(requestDto), CertificateOperationStatusRequestDto.class)
                        .retrieve()
                        .toEntity(CertificateOperationStatusResponseDto.class), "getRegisterStatus"),
                request,
                connector);
    }

    @Override
    public ResponseEntity<Void> cancelRegister(ApiClientConnectorInfo connector, CertificateOperationCancelRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> requireResponse(r
                        .uri(connector.getUrl() + CERTIFICATE_REGISTER_CANCEL_CONTEXT)
                        .body(Mono.just(requestDto), CertificateOperationCancelRequestDto.class)
                        .retrieve()
                        .toBodilessEntity(), "cancelRegister"),
                request,
                connector);
    }

    // ---- Identify ----

    @Override
    public CertificateIdentificationResponseDto identify(ApiClientConnectorInfo connector, CertificateIdentificationRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> requireBody(r
                        .uri(connector.getUrl() + CERTIFICATE_IDENTIFY_CONTEXT)
                        .body(Mono.just(requestDto), CertificateIdentificationRequestDto.class)
                        .retrieve()
                        .toEntity(CertificateIdentificationResponseDto.class), "identify"),
                request,
                connector);
    }
}
