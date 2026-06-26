package com.otilm.api.clients.mq.signing;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.interfaces.client.v1.signing.SignatureFormattingSyncApiClient;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.signatures.formatting.FormatDtbsRequestDto;
import com.otilm.api.model.connector.signatures.formatting.FormatDtbsResponseDto;
import com.otilm.api.model.connector.signatures.formatting.FormatResponseRequestDto;
import com.otilm.api.model.connector.signatures.formatting.FormattedResponseDto;
import com.otilm.api.clients.mq.ProxyClient;

import java.util.Arrays;
import java.util.List;

public class SignatureFormattingApiClient implements SignatureFormattingSyncApiClient {

    private static final String BASE_PATH = "/v1/signatureProvider/formatting";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";

    private final ProxyClient proxyClient;

    public SignatureFormattingApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public List<BaseAttribute> listFormattingAttributes(ApiClientConnectorInfo connector) throws ConnectorException {
        String path = BASE_PATH + "/attributes";
        BaseAttribute[] result = proxyClient.sendRequest(connector, path, HTTP_METHOD_GET, null, BaseAttribute[].class);
        return result == null ? List.of() : Arrays.asList(result);
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
