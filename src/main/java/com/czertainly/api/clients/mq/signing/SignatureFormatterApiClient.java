package com.czertainly.api.clients.mq.signing;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v1.signing.SignatureFormatterSyncApiClient;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.signatures.formatter.FormatDtbsRequestDto;
import com.czertainly.api.model.connector.signatures.formatter.FormatDtbsResponseDto;
import com.czertainly.api.model.connector.signatures.formatter.FormatResponseRequestDto;
import com.czertainly.api.model.connector.signatures.formatter.FormattedResponseDto;
import com.czertainly.api.clients.mq.ProxyClient;

import java.util.Arrays;
import java.util.List;

public class SignatureFormatterApiClient implements SignatureFormatterSyncApiClient {

    private static final String BASE_PATH = "/v1/signatureProvider/formatting";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";

    private final ProxyClient proxyClient;

    public SignatureFormatterApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public List<BaseAttribute> listFormatterAttributes(ApiClientConnectorInfo connector) throws ConnectorException {
        String path = BASE_PATH + "/attributes";
        BaseAttribute[] result = proxyClient.sendRequest(connector, path, HTTP_METHOD_GET, null, BaseAttribute[].class);
        return Arrays.asList(result);
    }

    @Override
    public FormatDtbsResponseDto formatDtbs(ApiClientConnectorInfo connector, FormatDtbsRequestDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/formatDtbs";
        return proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, requestDto, FormatDtbsResponseDto.class);
    }

    @Override
    public FormattedResponseDto formatSigningResponse(ApiClientConnectorInfo connector, FormatResponseRequestDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/formatResponse";
        return proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, requestDto, FormattedResponseDto.class);
    }
}
