package com.czertainly.api.clients.mq.v2;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.clients.mq.ProxyClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.interfaces.client.v2.CertificateSyncApiClient;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.v2.*;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * MQ-based implementation of v2 Certificate API client.
 *
 * <p>The path constants below are part of the v2 connector API contract — they describe
 * the routes a connector implementation must expose, not URIs that are configurable per
 * environment. This is the same pattern as the sibling WebClient-based
 * {@link com.czertainly.api.clients.v2.CertificateApiClient}, where paths like
 * {@code /v2/authorityProvider/authorities/{uuid}/certificates/issue} are also hardcoded.</p>
 */
@SuppressWarnings("java:S1075") // contract paths, not configurable URIs
public class CertificateApiClient implements CertificateSyncApiClient {

    private static final String BASE_PATH = "/v2/authorityProvider/authorities";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";

    // Path templates relative to the authority instance under BASE_PATH/{uuid}.
    private static final String PATH_ISSUE_ATTRIBUTES = "/certificates/issue/attributes";
    private static final String PATH_ISSUE_ATTRIBUTES_VALIDATE = "/certificates/issue/attributes/validate";
    private static final String PATH_ISSUE = "/certificates/issue";
    private static final String PATH_RENEW = "/certificates/renew";
    private static final String PATH_REVOKE_ATTRIBUTES = "/certificates/revoke/attributes";
    private static final String PATH_REVOKE_ATTRIBUTES_VALIDATE = "/certificates/revoke/attributes/validate";
    private static final String PATH_REVOKE = "/certificates/revoke";
    private static final String PATH_IDENTIFY = "/certificates/identify";
    private static final String PATH_ISSUE_CANCEL = "/certificates/issue/cancel";
    private static final String PATH_REVOKE_CANCEL = "/certificates/revoke/cancel";
    private static final String PATH_ISSUE_STATUS = "/certificates/issue/status";
    private static final String PATH_REVOKE_STATUS = "/certificates/revoke/status";

    private static String authorityPath(String authorityUuid, String suffix) {
        return BASE_PATH + "/" + authorityUuid + suffix;
    }

    private final ProxyClient proxyClient;

    public CertificateApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public List<BaseAttribute> listIssueCertificateAttributes(ApiClientConnectorInfo connector, String authorityUuid) throws ConnectorException {
        String path = authorityPath(authorityUuid, PATH_ISSUE_ATTRIBUTES);
        BaseAttribute[] result = proxyClient.sendRequest(connector, path, HTTP_METHOD_GET, null, BaseAttribute[].class);
        return Arrays.asList(result);
    }

    @Override
    public Boolean validateIssueCertificateAttributes(ApiClientConnectorInfo connector, String authorityUuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException {
        String path = authorityPath(authorityUuid, PATH_ISSUE_ATTRIBUTES_VALIDATE);
        return proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, attributes, Boolean.class);
    }

    @Override
    public ResponseEntity<CertificateDataResponseDto> issueCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertificateSignRequestDto requestDto) throws ConnectorException {
        String path = authorityPath(authorityUuid, PATH_ISSUE);
        // sendRequestForEntity preserves the upstream HTTP status so 202 Accepted is not
        // collapsed to 200 OK by the proxy hop — required for asynchronous-issuance detection.
        return proxyClient.sendRequestForEntity(connector, path, HTTP_METHOD_POST, requestDto, CertificateDataResponseDto.class);
    }

    @Override
    public ResponseEntity<CertificateDataResponseDto> renewCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertificateRenewRequestDto requestDto) throws ConnectorException {
        String path = authorityPath(authorityUuid, PATH_RENEW);
        return proxyClient.sendRequestForEntity(connector, path, HTTP_METHOD_POST, requestDto, CertificateDataResponseDto.class);
    }

    @Override
    public List<BaseAttribute> listRevokeCertificateAttributes(ApiClientConnectorInfo connector, String authorityUuid) throws ConnectorException {
        String path = authorityPath(authorityUuid, PATH_REVOKE_ATTRIBUTES);
        BaseAttribute[] result = proxyClient.sendRequest(connector, path, HTTP_METHOD_GET, null, BaseAttribute[].class);
        return Arrays.asList(result);
    }

    @Override
    public Boolean validateRevokeCertificateAttributes(ApiClientConnectorInfo connector, String authorityUuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException {
        String path = authorityPath(authorityUuid, PATH_REVOKE_ATTRIBUTES_VALIDATE);
        return proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, attributes, Boolean.class);
    }

    @Override
    public ResponseEntity<CertificateDataResponseDto> revokeCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertRevocationDto requestDto) throws ConnectorException {
        String path = authorityPath(authorityUuid, PATH_REVOKE);
        // Preserve the upstream HTTP status so 202 Accepted (asynchronous revoke) is not
        // collapsed to 200 OK. The body is empty for revoke regardless of status, but the
        // status is the signal Core uses to decide PENDING_REVOKE vs REVOKED.
        return proxyClient.sendRequestForEntity(connector, path, HTTP_METHOD_POST, requestDto, CertificateDataResponseDto.class);
    }

    @Override
    public CertificateIdentificationResponseDto identifyCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertificateIdentificationRequestDto requestDto) throws ValidationException, ConnectorException {
        String path = authorityPath(authorityUuid, PATH_IDENTIFY);
        return proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, requestDto, CertificateIdentificationResponseDto.class);
    }

    @Override
    public void cancelIssueCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertificateOperationCancelRequestDto requestDto) throws ValidationException, ConnectorException {
        String path = authorityPath(authorityUuid, PATH_ISSUE_CANCEL);
        proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, requestDto, Void.class);
    }

    @Override
    public void cancelRevokeCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertificateOperationCancelRequestDto requestDto) throws ValidationException, ConnectorException {
        String path = authorityPath(authorityUuid, PATH_REVOKE_CANCEL);
        proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, requestDto, Void.class);
    }

    @Override
    public CertificateOperationStatusResponseDto getIssueCertificateStatus(ApiClientConnectorInfo connector, String authorityUuid, CertificateOperationStatusRequestDto requestDto) throws ConnectorException {
        String path = authorityPath(authorityUuid, PATH_ISSUE_STATUS);
        return proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, requestDto, CertificateOperationStatusResponseDto.class);
    }

    @Override
    public CertificateOperationStatusResponseDto getRevokeCertificateStatus(ApiClientConnectorInfo connector, String authorityUuid, CertificateOperationStatusRequestDto requestDto) throws ConnectorException {
        String path = authorityPath(authorityUuid, PATH_REVOKE_STATUS);
        return proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, requestDto, CertificateOperationStatusResponseDto.class);
    }

    // Async variants
    public CompletableFuture<CertificateDataResponseDto> issueCertificateAsync(ApiClientConnectorInfo connector, String authorityUuid, CertificateSignRequestDto requestDto) {
        String path = authorityPath(authorityUuid, PATH_ISSUE);
        return proxyClient.sendRequestAsync(connector, path, HTTP_METHOD_POST, requestDto, CertificateDataResponseDto.class);
    }

    public CompletableFuture<CertificateDataResponseDto> renewCertificateAsync(ApiClientConnectorInfo connector, String authorityUuid, CertificateRenewRequestDto requestDto) {
        String path = authorityPath(authorityUuid, PATH_RENEW);
        return proxyClient.sendRequestAsync(connector, path, HTTP_METHOD_POST, requestDto, CertificateDataResponseDto.class);
    }
}
