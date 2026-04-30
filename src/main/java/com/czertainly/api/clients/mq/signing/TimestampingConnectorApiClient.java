package com.czertainly.api.clients.mq.signing;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.signatures.formatter.FormatDtbsResponseDto;
import com.czertainly.api.model.connector.signatures.formatter.FormattedResponseDto;
import com.czertainly.api.model.connector.signatures.formatter.TimestampingFormatDtbsRequestDto;
import com.czertainly.api.model.connector.signatures.formatter.TimestampingFormatResponseRequestDto;
import com.czertainly.api.clients.mq.ProxyClient;

import java.util.Arrays;
import java.util.List;

public class TimestampingConnectorApiClient {

    private static final String BASE_PATH = "/v1/signatureProvider/formatting";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";

    private final ProxyClient proxyClient;

    public TimestampingConnectorApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    public List<BaseAttribute> listFormatterAttributes(ApiClientConnectorInfo connector) throws ConnectorException {
        String path = BASE_PATH + "/attributes";
        BaseAttribute[] result = proxyClient.sendRequest(connector, path, HTTP_METHOD_GET, null, BaseAttribute[].class);
        return Arrays.asList(result);
    }

    public FormatDtbsResponseDto formatDtbs(ApiClientConnectorInfo connector, TimestampingFormatDtbsRequestDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/formatDtbs";
        return proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, requestDto, FormatDtbsResponseDto.class);
    }

    public FormattedResponseDto formatSigningResponse(ApiClientConnectorInfo connector, TimestampingFormatResponseRequestDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/formatResponse";
        return proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, requestDto, FormattedResponseDto.class);
    }
}
