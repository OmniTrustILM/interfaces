package com.otilm.api.clients.mq.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.mq.ProxyClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.interfaces.client.v2.TokenSyncApiClient;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.v2.OperationResponseValidator;
import com.otilm.api.model.connector.cryptography.v2.OperationValidationResult;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenStatusResponseV2Dto;

import java.util.Arrays;
import java.util.List;

/**
 * MQ implementation of the connector-facing v2 stateless token API.
 */
@SuppressWarnings("java:S1075")
public class TokenApiClient implements TokenSyncApiClient {

    private static final String BASE_PATH = "/v2/cryptographyProvider/tokens";
    private static final String KEY_REQUEST_TYPES_PATH = BASE_PATH + "/keyRequestTypes";
    private static final String ATTRIBUTES_PATH = BASE_PATH + "/attributes";
    private static final String STATUS_PATH = BASE_PATH + "/status";
    private static final String PROFILE_ATTRIBUTES_PATH = BASE_PATH + "/tokenProfile/attributes";
    private static final String GET = "GET";
    private static final String POST = "POST";

    private final ProxyClient proxyClient;
    private final OperationResponseValidator responseValidator;

    public TokenApiClient(ProxyClient proxyClient, OperationResponseValidator responseValidator) {
        this.proxyClient = proxyClient;
        this.responseValidator = responseValidator;
    }

    private static void requireValid(OperationValidationResult validation, ApiClientConnectorInfo connector)
            throws ConnectorException {
        if (!validation.isValid()) {
            IllegalArgumentException cause = validation.getCause();
            throw new ConnectorException(cause.getMessage(), cause, connector);
        }
    }

    @Override
    public List<BaseAttribute> listTokenAttributes(ApiClientConnectorInfo connector) throws ConnectorException {
        try {
            List<BaseAttribute> response = sendForList(connector, ATTRIBUTES_PATH, GET, null, BaseAttribute[].class,
                    "listTokenAttributes");
            requireValid(responseValidator.validateAttributeList(response), connector);
            return response;
        } catch (RuntimeException e) {
            throw new ConnectorException("List token attributes request has failed", e, connector);
        }
    }

    @Override
    public TokenStatusResponseV2Dto getTokenStatus(ApiClientConnectorInfo connector, TokenScopedRequestV2Dto request)
            throws ConnectorException {
        try {
            TokenStatusResponseV2Dto response = proxyClient
                    .sendRequest(connector, STATUS_PATH, POST, request, TokenStatusResponseV2Dto.class);
            requireValid(responseValidator.validateTokenStatus(response), connector);
            return response;
        } catch (RuntimeException e) {
            throw new ConnectorException("Get token status request has failed", e, connector);
        }
    }

    @Override
    public List<BaseAttribute> listTokenProfileAttributes(ApiClientConnectorInfo connector,
            TokenScopedRequestV2Dto request) throws ConnectorException {
        try {
            List<BaseAttribute> response = sendForList(connector, PROFILE_ATTRIBUTES_PATH, POST, request,
                    BaseAttribute[].class, "listTokenProfileAttributes");
            requireValid(responseValidator.validateAttributeList(response), connector);
            return response;
        } catch (RuntimeException e) {
            throw new ConnectorException("get token profile attributes request has failed", e, connector);
        }
    }

    @Override
    public List<KeyRequestType> listSupportedKeyRequestTypes(ApiClientConnectorInfo connector,
            TokenProfileScopedRequestV2Dto request) throws ConnectorException {
        try {
            List<KeyRequestType> response = sendForList(connector, KEY_REQUEST_TYPES_PATH, POST, request,
                    KeyRequestType[].class, "listSupportedKeyRequestTypes");
            requireValid(responseValidator.validateSupportedKeyRequestTypes(response), connector);
            return response;
        } catch (RuntimeException e) {
            throw new ConnectorException("Get token key request types request has failed", e, connector);
        }
    }

    private <T> List<T> sendForList(ApiClientConnectorInfo connector, String path, String method, Object body,
            Class<T[]> responseType, String operation) throws ConnectorException {
        T[] response = proxyClient.sendRequest(connector, path, method, body, responseType);
        if (response == null) {
            throw new ConnectorException("Connector returned an empty body for " + operation, connector);
        }
        return Arrays.asList(response);
    }
}
