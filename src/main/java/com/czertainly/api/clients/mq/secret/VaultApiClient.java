package com.czertainly.api.clients.mq.secret;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.clients.mq.ProxyClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v1.secret.VaultSyncApiClient;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VaultApiClient implements VaultSyncApiClient {

    private static final String VAULT_BASE_PATH = "/v1/secretProvider/vaults";
    private static final String VAULT_PROFILE_BASE_PATH = "/v1/secretProvider/vaultProfiles";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";

    private final ProxyClient proxyClient;

    public VaultApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public void checkVaultConnection(ApiClientConnectorInfo connector, List<RequestAttribute> attributes) throws ConnectorException {
        proxyClient.sendRequest(
                connector,
                VAULT_BASE_PATH,
                HTTP_METHOD_POST,
                attributes,
                Void.class
        );
    }

    @Override
    public List<BaseAttribute> listVaultAttributes(ApiClientConnectorInfo connector) throws ConnectorException {
        BaseAttribute[] result = proxyClient.sendRequest(
                connector,
                VAULT_BASE_PATH + "/attributes",
                HTTP_METHOD_GET,
                null,
                BaseAttribute[].class
        );
        return Arrays.asList(result);
    }

    @Override
    public List<BaseAttribute> listVaultProfileAttributes(ApiClientConnectorInfo connector, List<RequestAttribute> attributes) throws ConnectorException {
        BaseAttribute[] result = proxyClient.sendRequest(
                connector,
                VAULT_PROFILE_BASE_PATH + "/attributes",
                HTTP_METHOD_POST,
                attributes,
                BaseAttribute[].class
        );
        return Arrays.asList(result);
    }

    // ==================== Async variants ====================

    public CompletableFuture<Void> checkVaultConnectionAsync(ApiClientConnectorInfo connector, List<RequestAttribute> attributes) {
        return proxyClient.sendRequestAsync(
                connector,
                VAULT_BASE_PATH,
                HTTP_METHOD_POST,
                attributes,
                Void.class
        );
    }

    public CompletableFuture<List<BaseAttribute>> listVaultAttributesAsync(ApiClientConnectorInfo connector) {
        return proxyClient.sendRequestAsync(
                connector,
                VAULT_BASE_PATH + "/attributes",
                HTTP_METHOD_GET,
                null,
                BaseAttribute[].class
        ).thenApply(Arrays::asList);
    }

    public CompletableFuture<List<BaseAttribute>> listVaultProfileAttributesAsync(ApiClientConnectorInfo connector, List<RequestAttribute> attributes) {
        return proxyClient.sendRequestAsync(
                connector,
                VAULT_PROFILE_BASE_PATH + "/attributes",
                HTTP_METHOD_POST,
                attributes,
                BaseAttribute[].class
        ).thenApply(Arrays::asList);
    }
}
