package com.otilm.api.clients.mq.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.mq.ProxyClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.interfaces.client.v2.TokenSyncApiClient;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.v2.token.TokenScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenStatusResponseV2Dto;
import com.otilm.api.model.core.cryptography.key.KeyUsage;

import java.util.Arrays;
import java.util.List;

/**
 * MQ implementation of the connector-facing v2 stateless token API.
 */
@SuppressWarnings("java:S1075")
public class TokenApiClient implements TokenSyncApiClient {

    private static final String BASE_PATH = "/v2/cryptographyProvider/tokens";
    private static final String ATTRIBUTES_PATH = BASE_PATH + "/attributes";
    private static final String STATUS_PATH = BASE_PATH + "/status";
    private static final String PROFILE_ATTRIBUTES_PATH = BASE_PATH + "/tokenProfile/attributes";
    private static final String PROFILE_KEY_USAGES_PATH = BASE_PATH + "/tokenProfile/keyUsages";
    private static final String GET = "GET";
    private static final String POST = "POST";

    private final ProxyClient proxyClient;

    public TokenApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public List<BaseAttribute> listTokenAttributes(ApiClientConnectorInfo connector) throws ConnectorException {
        try {
            return Arrays.asList(proxyClient.sendRequest(connector, ATTRIBUTES_PATH, GET, null, BaseAttribute[].class));
        } catch (ConnectorException | RuntimeException e) {
            throw new ConnectorException("Token attribute request failed", connector);
        }
    }

    @Override
    public TokenStatusResponseV2Dto getTokenStatus(ApiClientConnectorInfo connector,
                                                   TokenScopedRequestV2Dto request) throws ConnectorException {
        try {
            return proxyClient.sendRequest(connector, STATUS_PATH, POST, request, TokenStatusResponseV2Dto.class);
        } catch (ConnectorException | RuntimeException e) {
            throw new ConnectorException("Token status request failed", connector);
        }
    }

    @Override
    public List<BaseAttribute> listTokenProfileAttributes(ApiClientConnectorInfo connector,
                                                          TokenScopedRequestV2Dto request) throws ConnectorException {
        try {
            return Arrays.asList(proxyClient.sendRequest(connector, PROFILE_ATTRIBUTES_PATH, POST, request, BaseAttribute[].class));
        } catch (ConnectorException | RuntimeException e) {
            throw new ConnectorException("Token profile attribute request failed", connector);
        }
    }

    @Override
    public List<KeyUsage> listTokenProfileKeyUsages(ApiClientConnectorInfo connector,
                                                    TokenScopedRequestV2Dto request) throws ConnectorException {
        try {
            return Arrays.asList(proxyClient.sendRequest(connector, PROFILE_KEY_USAGES_PATH, POST, request, KeyUsage[].class));
        } catch (ConnectorException | RuntimeException e) {
            throw new ConnectorException("Token profile key-usage request failed", connector);
        }
    }
}
