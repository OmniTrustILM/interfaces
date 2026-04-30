package com.czertainly.api.clients.mq;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.interfaces.client.v1.KeyManagementSyncApiClient;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.cryptography.key.CreateKeyRequestDto;
import com.czertainly.api.model.connector.cryptography.key.KeyDataResponseDto;
import com.czertainly.api.model.connector.cryptography.key.KeyPairDataResponseDto;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class KeyManagementApiClient implements KeyManagementSyncApiClient {

    private static final String BASE_PATH = "/v1/cryptographyProvider/tokens";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";
    private static final String HTTP_METHOD_DELETE = "DELETE";

    private final ProxyClient proxyClient;

    public KeyManagementApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public List<BaseAttribute> listCreateSecretKeyAttributes(ApiClientConnectorInfo connector, String uuid) throws ConnectorException {
        String path = BASE_PATH + "/" + uuid + "/keys/secret/attributes";
        BaseAttribute[] result = proxyClient.sendRequest(connector, path, HTTP_METHOD_GET, null, BaseAttribute[].class);
        return Arrays.asList(result);
    }

    @Override
    public void validateCreateSecretKeyAttributes(ApiClientConnectorInfo connector, String uuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException {
        String path = BASE_PATH + "/" + uuid + "/keys/secret/attributes/validate";
        proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, attributes, Void.class);
    }

    @Override
    public KeyDataResponseDto createSecretKey(ApiClientConnectorInfo connector, String uuid, CreateKeyRequestDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/" + uuid + "/keys/secret";
        return proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, requestDto, KeyDataResponseDto.class);
    }

    @Override
    public List<BaseAttribute> listCreateKeyPairAttributes(ApiClientConnectorInfo connector, String uuid) throws ConnectorException {
        String path = BASE_PATH + "/" + uuid + "/keys/pair/attributes";
        BaseAttribute[] result = proxyClient.sendRequest(connector, path, HTTP_METHOD_GET, null, BaseAttribute[].class);
        return Arrays.asList(result);
    }

    @Override
    public void validateCreateKeyPairAttributes(ApiClientConnectorInfo connector, String uuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException {
        String path = BASE_PATH + "/" + uuid + "/keys/pair/attributes/validate";
        proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, attributes, Void.class);
    }

    @Override
    public KeyPairDataResponseDto createKeyPair(ApiClientConnectorInfo connector, String uuid, CreateKeyRequestDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/" + uuid + "/keys/pair";
        return proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, requestDto, KeyPairDataResponseDto.class);
    }

    @Override
    public List<KeyDataResponseDto> listKeys(ApiClientConnectorInfo connector, String uuid) throws ConnectorException {
        String path = BASE_PATH + "/" + uuid + "/keys";
        KeyDataResponseDto[] result = proxyClient.sendRequest(connector, path, HTTP_METHOD_GET, null, KeyDataResponseDto[].class);
        return Arrays.asList(result);
    }

    @Override
    public KeyDataResponseDto getKey(ApiClientConnectorInfo connector, String uuid, String keyUuid) throws ConnectorException {
        String path = BASE_PATH + "/" + uuid + "/keys/" + keyUuid;
        return proxyClient.sendRequest(connector, path, HTTP_METHOD_GET, null, KeyDataResponseDto.class);
    }

    @Override
    public void destroyKey(ApiClientConnectorInfo connector, String uuid, String keyUuid) throws ConnectorException {
        String path = BASE_PATH + "/" + uuid + "/keys/" + keyUuid;
        proxyClient.sendRequest(connector, path, HTTP_METHOD_DELETE, null, Void.class);
    }

    // Async variants
    public CompletableFuture<List<KeyDataResponseDto>> listKeysAsync(ApiClientConnectorInfo connector, String uuid) {
        String path = BASE_PATH + "/" + uuid + "/keys";
        return proxyClient.sendRequestAsync(connector, path, HTTP_METHOD_GET, null, KeyDataResponseDto[].class)
                .thenApply(Arrays::asList);
    }

    public CompletableFuture<KeyPairDataResponseDto> createKeyPairAsync(ApiClientConnectorInfo connector, String uuid, CreateKeyRequestDto requestDto) {
        String path = BASE_PATH + "/" + uuid + "/keys/pair";
        return proxyClient.sendRequestAsync(connector, path, HTTP_METHOD_POST, requestDto, KeyPairDataResponseDto.class);
    }
}
