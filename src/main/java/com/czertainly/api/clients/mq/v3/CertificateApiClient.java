package com.czertainly.api.clients.mq.v3;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.clients.mq.ProxyClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v3.CertificateSyncApiClient;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.v3.certificate.CertificateAttributeListRequestDto;
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
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

/**
 * MQ-based implementation of v3 Certificate API client.
 * Mirror of {@link com.czertainly.api.clients.v3.CertificateApiClient}.
 *
 * <p>Sync/async endpoints use {@link ProxyClient#sendRequestForEntity} so the upstream HTTP status
 * (200 vs 202) is preserved across the MQ hop.</p>
 */
@SuppressWarnings("java:S1075") // contract paths, not configurable URIs
public class CertificateApiClient implements CertificateSyncApiClient {

    private static final String BASE_PATH = "/v3/authorityProvider/certificates";

    private static final String PATH_ISSUE_ATTRIBUTES = BASE_PATH + "/issue/attributes";
    private static final String PATH_ISSUE = BASE_PATH + "/issue";
    private static final String PATH_ISSUE_STATUS = BASE_PATH + "/issue/status";
    private static final String PATH_ISSUE_CANCEL = BASE_PATH + "/issue/cancel";

    private static final String PATH_RENEW = BASE_PATH + "/renew";

    private static final String PATH_REVOKE_ATTRIBUTES = BASE_PATH + "/revoke/attributes";
    private static final String PATH_REVOKE = BASE_PATH + "/revoke";
    private static final String PATH_REVOKE_STATUS = BASE_PATH + "/revoke/status";
    private static final String PATH_REVOKE_CANCEL = BASE_PATH + "/revoke/cancel";

    private static final String PATH_REGISTER_ATTRIBUTES = BASE_PATH + "/register/attributes";
    private static final String PATH_REGISTER = BASE_PATH + "/register";
    private static final String PATH_REGISTER_STATUS = BASE_PATH + "/register/status";
    private static final String PATH_REGISTER_CANCEL = BASE_PATH + "/register/cancel";

    private static final String PATH_IDENTIFY = BASE_PATH + "/identify";

    private static final String HTTP_METHOD_POST = "POST";

    private final ProxyClient proxyClient;

    public CertificateApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    // ---- Issue ----

    @Override
    public List<BaseAttribute> listIssueAttributes(ApiClientConnectorInfo connector, CertificateAttributeListRequestDto requestDto) throws ConnectorException {
        BaseAttribute[] result = proxyClient.sendRequest(connector, PATH_ISSUE_ATTRIBUTES, HTTP_METHOD_POST, requestDto, BaseAttribute[].class);
        return Arrays.asList(result);
    }

    @Override
    public ResponseEntity<CertificateDataResponseDto> issue(ApiClientConnectorInfo connector, CertificateSignRequestDto requestDto) throws ConnectorException {
        return proxyClient.sendRequestForEntity(connector, PATH_ISSUE, HTTP_METHOD_POST, requestDto, CertificateDataResponseDto.class);
    }

    @Override
    public CertificateOperationStatusResponseDto getIssueStatus(ApiClientConnectorInfo connector, CertificateOperationStatusRequestDto requestDto) throws ConnectorException {
        return proxyClient.sendRequest(connector, PATH_ISSUE_STATUS, HTTP_METHOD_POST, requestDto, CertificateOperationStatusResponseDto.class);
    }

    @Override
    public ResponseEntity<Void> cancelIssue(ApiClientConnectorInfo connector, CertificateOperationCancelRequestDto requestDto) throws ConnectorException {
        return proxyClient.sendRequestForEntity(connector, PATH_ISSUE_CANCEL, HTTP_METHOD_POST, requestDto, Void.class);
    }

    // ---- Renew (status/cancel via /issue/*) ----

    @Override
    public ResponseEntity<CertificateDataResponseDto> renew(ApiClientConnectorInfo connector, CertificateRenewRequestDto requestDto) throws ConnectorException {
        return proxyClient.sendRequestForEntity(connector, PATH_RENEW, HTTP_METHOD_POST, requestDto, CertificateDataResponseDto.class);
    }

    // ---- Revoke ----

    @Override
    public List<BaseAttribute> listRevokeAttributes(ApiClientConnectorInfo connector, CertificateAttributeListRequestDto requestDto) throws ConnectorException {
        BaseAttribute[] result = proxyClient.sendRequest(connector, PATH_REVOKE_ATTRIBUTES, HTTP_METHOD_POST, requestDto, BaseAttribute[].class);
        return Arrays.asList(result);
    }

    @Override
    public ResponseEntity<CertificateDataResponseDto> revoke(ApiClientConnectorInfo connector, CertificateRevocationRequestDto requestDto) throws ConnectorException {
        return proxyClient.sendRequestForEntity(connector, PATH_REVOKE, HTTP_METHOD_POST, requestDto, CertificateDataResponseDto.class);
    }

    @Override
    public CertificateOperationStatusResponseDto getRevokeStatus(ApiClientConnectorInfo connector, CertificateOperationStatusRequestDto requestDto) throws ConnectorException {
        return proxyClient.sendRequest(connector, PATH_REVOKE_STATUS, HTTP_METHOD_POST, requestDto, CertificateOperationStatusResponseDto.class);
    }

    @Override
    public ResponseEntity<Void> cancelRevoke(ApiClientConnectorInfo connector, CertificateOperationCancelRequestDto requestDto) throws ConnectorException {
        return proxyClient.sendRequestForEntity(connector, PATH_REVOKE_CANCEL, HTTP_METHOD_POST, requestDto, Void.class);
    }

    // ---- Register ----

    @Override
    public List<BaseAttribute> listRegisterAttributes(ApiClientConnectorInfo connector, CertificateAttributeListRequestDto requestDto) throws ConnectorException {
        BaseAttribute[] result = proxyClient.sendRequest(connector, PATH_REGISTER_ATTRIBUTES, HTTP_METHOD_POST, requestDto, BaseAttribute[].class);
        return Arrays.asList(result);
    }

    @Override
    public ResponseEntity<CertificateDataResponseDto> register(ApiClientConnectorInfo connector, CertificateRegistrationRequestDto requestDto) throws ConnectorException {
        return proxyClient.sendRequestForEntity(connector, PATH_REGISTER, HTTP_METHOD_POST, requestDto, CertificateDataResponseDto.class);
    }

    @Override
    public CertificateOperationStatusResponseDto getRegisterStatus(ApiClientConnectorInfo connector, CertificateOperationStatusRequestDto requestDto) throws ConnectorException {
        return proxyClient.sendRequest(connector, PATH_REGISTER_STATUS, HTTP_METHOD_POST, requestDto, CertificateOperationStatusResponseDto.class);
    }

    @Override
    public ResponseEntity<Void> cancelRegister(ApiClientConnectorInfo connector, CertificateOperationCancelRequestDto requestDto) throws ConnectorException {
        return proxyClient.sendRequestForEntity(connector, PATH_REGISTER_CANCEL, HTTP_METHOD_POST, requestDto, Void.class);
    }

    // ---- Identify ----

    @Override
    public CertificateIdentificationResponseDto identify(ApiClientConnectorInfo connector, CertificateIdentificationRequestDto requestDto) throws ConnectorException {
        return proxyClient.sendRequest(connector, PATH_IDENTIFY, HTTP_METHOD_POST, requestDto, CertificateIdentificationResponseDto.class);
    }
}
