package com.czertainly.api.clients.mq.v2;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.clients.mq.ProxyClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.interfaces.client.v2.CertificateSyncApiClient;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.v2.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * MQ-based implementation of v2 Certificate API client.
 */
public class CertificateApiClient implements CertificateSyncApiClient {

    private static final String BASE_PATH = "/v2/authorityProvider/authorities";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";

    private final ProxyClient proxyClient;

    public CertificateApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public List<BaseAttribute> listIssueCertificateAttributes(ApiClientConnectorInfo connector, String authorityUuid) throws ConnectorException {
        String path = BASE_PATH + "/" + authorityUuid + "/certificates/issue/attributes";
        BaseAttribute[] result = proxyClient.sendRequest(connector, path, HTTP_METHOD_GET, null, BaseAttribute[].class);
        return Arrays.asList(result);
    }

    @Override
    public Boolean validateIssueCertificateAttributes(ApiClientConnectorInfo connector, String authorityUuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException {
        String path = BASE_PATH + "/" + authorityUuid + "/certificates/issue/attributes/validate";
        return proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, attributes, Boolean.class);
    }

    @Override
    public CertificateDataResponseDto issueCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertificateSignRequestDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/" + authorityUuid + "/certificates/issue";
        return proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, requestDto, CertificateDataResponseDto.class);
    }

    @Override
    public CertificateDataResponseDto renewCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertificateRenewRequestDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/" + authorityUuid + "/certificates/renew";
        return proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, requestDto, CertificateDataResponseDto.class);
    }

    @Override
    public List<BaseAttribute> listRevokeCertificateAttributes(ApiClientConnectorInfo connector, String authorityUuid) throws ConnectorException {
        String path = BASE_PATH + "/" + authorityUuid + "/certificates/revoke/attributes";
        BaseAttribute[] result = proxyClient.sendRequest(connector, path, HTTP_METHOD_GET, null, BaseAttribute[].class);
        return Arrays.asList(result);
    }

    @Override
    public Boolean validateRevokeCertificateAttributes(ApiClientConnectorInfo connector, String authorityUuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException {
        String path = BASE_PATH + "/" + authorityUuid + "/certificates/revoke/attributes/validate";
        return proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, attributes, Boolean.class);
    }

    @Override
    public void revokeCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertRevocationDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/" + authorityUuid + "/certificates/revoke";
        proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, requestDto, Void.class);
    }

    @Override
    public CertificateIdentificationResponseDto identifyCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertificateIdentificationRequestDto requestDto) throws ValidationException, ConnectorException {
        String path = BASE_PATH + "/" + authorityUuid + "/certificates/identify";
        return proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, requestDto, CertificateIdentificationResponseDto.class);
    }

    // Async variants
    public CompletableFuture<CertificateDataResponseDto> issueCertificateAsync(ApiClientConnectorInfo connector, String authorityUuid, CertificateSignRequestDto requestDto) {
        String path = BASE_PATH + "/" + authorityUuid + "/certificates/issue";
        return proxyClient.sendRequestAsync(connector, path, HTTP_METHOD_POST, requestDto, CertificateDataResponseDto.class);
    }

    public CompletableFuture<CertificateDataResponseDto> renewCertificateAsync(ApiClientConnectorInfo connector, String authorityUuid, CertificateRenewRequestDto requestDto) {
        String path = BASE_PATH + "/" + authorityUuid + "/certificates/renew";
        return proxyClient.sendRequestAsync(connector, path, HTTP_METHOD_POST, requestDto, CertificateDataResponseDto.class);
    }
}
