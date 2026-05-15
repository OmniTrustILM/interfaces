package com.czertainly.api.clients.mq.signing;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v1.signing.SignatureFormatterSyncApiClient;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.clients.mq.ProxyClient;

import java.util.Arrays;
import java.util.List;

public class SignatureFormatterApiClient implements SignatureFormatterSyncApiClient {

    private static final String BASE_PATH = "/v1/signatureProvider/formatting";
    private static final String HTTP_METHOD_GET = "GET";

    private final ProxyClient proxyClient;

    public SignatureFormatterApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public List<BaseAttribute> listFormatterAttributes(ApiClientConnectorInfo connector) throws ConnectorException {
        String path = BASE_PATH + "/attributes";
        BaseAttribute[] result = proxyClient.sendRequest(connector, path, HTTP_METHOD_GET, null, BaseAttribute[].class);
        return result == null ? List.of() : Arrays.asList(result);
    }
}
