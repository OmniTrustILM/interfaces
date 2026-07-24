package com.otilm.api.clients.mq.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.mq.ProxyClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.interfaces.client.v2.KeySyncApiClient;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.cryptography.v2.OperationResponseValidator;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.*;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

/**
 * MQ implementation of the connector-facing stateless cryptography v2 key API.
 */
@SuppressWarnings("java:S1075")
public class KeyApiClient implements KeySyncApiClient {

    private static final String BASE_PATH = "/v2/cryptographyProvider/keys";
    private static final String POST = "POST";
    private final ProxyClient proxyClient;

    public KeyApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public List<KeyRequestType> listSupportedKeyTypes(ApiClientConnectorInfo connector,
                                                      TokenProfileScopedRequestV2Dto request)
            throws ConnectorException {
        KeyRequestType[] response = send(connector, "/types", request, KeyRequestType[].class);
        if (response == null) {
            throw new ConnectorException("Connector returned an empty supported-key-types response", connector);
        }
        List<KeyRequestType> types = Arrays.asList(response);
        if (types.stream().anyMatch(java.util.Objects::isNull)) {
            throw new ConnectorException("Connector returned a null supported key type", connector);
        }
        return types;
    }

    @Override
    public List<BaseAttribute> listCreateKeyAttributes(ApiClientConnectorInfo connector, KeyRequestType type,
                                                       TokenProfileScopedRequestV2Dto request)
            throws ConnectorException {
        String segment = attributePath(type, connector);
        BaseAttribute[] response = send(connector, segment, request, BaseAttribute[].class);
        if (response == null) {
            throw new ConnectorException("Connector returned an empty key-attributes response", connector);
        }
        return Arrays.asList(response);
    }

    @Override
    public ResponseEntity<SecretKeyDataResponseV2Dto> createSecretKey(ApiClientConnectorInfo connector, CreateKeyRequestV2Dto request)
            throws ConnectorException {
        ResponseEntity<SecretKeyDataResponseV2Dto> response = sendEntity(connector, "/secret", request, SecretKeyDataResponseV2Dto.class);
        validateCreateResponse(connector, request, response);
        return response;
    }

    @Override
    public ResponseEntity<KeyPairDataResponseV2Dto> createKeyPair(ApiClientConnectorInfo connector, CreateKeyRequestV2Dto request)
            throws ConnectorException {
        ResponseEntity<KeyPairDataResponseV2Dto> response = sendEntity(connector, "/pair", request, KeyPairDataResponseV2Dto.class);
        validateCreateResponse(connector, request, response);
        return response;
    }

    @Override
    public KeyOperationStatusResponseV2Dto getCreateKeyStatus(ApiClientConnectorInfo c, KeyOperationStatusRequestV2Dto b) throws ConnectorException {
        return send(c, "/create/status", b, KeyOperationStatusResponseV2Dto.class);
    }

    @Override
    public ResponseEntity<Void> cancelCreateKey(ApiClientConnectorInfo c, KeyOperationCancelRequestV2Dto b) throws ConnectorException {
        return sendEntity(c, "/create/cancel", b, Void.class);
    }

    @Override
    public ResponseEntity<KeyDataResponseV2Dto> destroyKey(ApiClientConnectorInfo c, DestroyKeyRequestV2Dto b) throws ConnectorException {
        ResponseEntity<KeyDataResponseV2Dto> r = sendEntity(c, "/destroy", b, KeyDataResponseV2Dto.class);
        try {
            OperationResponseValidator.validateDestroy(b.getExecutionMode(), r);
        } catch (IllegalArgumentException e) {
            throw new ConnectorException(e.getMessage(), c);
        }
        return r;
    }

    @Override
    public KeyOperationStatusResponseV2Dto getDestroyKeyStatus(ApiClientConnectorInfo c, KeyOperationStatusRequestV2Dto b) throws ConnectorException {
        return send(c, "/destroy/status", b, KeyOperationStatusResponseV2Dto.class);
    }

    @Override
    public ResponseEntity<Void> cancelDestroyKey(ApiClientConnectorInfo c, KeyOperationCancelRequestV2Dto b) throws ConnectorException {
        return sendEntity(c, "/destroy/cancel", b, Void.class);
    }

    private <T> T send(ApiClientConnectorInfo connector, String path, Object body, Class<T> responseType)
            throws ConnectorException {
        try {
            return proxyClient.sendRequest(connector, BASE_PATH + path, POST, body, responseType);
        } catch (ConnectorException | RuntimeException e) {
            throw new ConnectorException("Key request failed", connector);
        }
    }

    private <T> ResponseEntity<T> sendEntity(ApiClientConnectorInfo connector, String path, Object body, Class<T> responseType)
            throws ConnectorException {
        try {
            return proxyClient.sendRequestForEntity(
                    connector, BASE_PATH + path, POST, body, responseType);
        } catch (ConnectorException | RuntimeException e) {
            throw new ConnectorException("Key request failed", connector);
        }
    }

    private static String attributePath(KeyRequestType type, ApiClientConnectorInfo connector)
            throws ConnectorException {
        if (type == null) {
            throw new ConnectorException("Key request type is required", connector);
        }
        return switch (type) {
            case SECRET -> "/secret/attributes";
            case KEY_PAIR -> "/pair/attributes";
        };
    }

    private static void validateCompletedResponse(ApiClientConnectorInfo connector, Object response)
            throws ConnectorException {
        try {
            if (response instanceof SecretKeyDataResponseV2Dto secretKey) {
                KeyResponseValidator.validateCompleted(secretKey);
            } else if (response instanceof KeyPairDataResponseV2Dto keyPair) {
                KeyResponseValidator.validateCompleted(keyPair);
            } else {
                throw new IllegalArgumentException("Unsupported key response type");
            }
        } catch (IllegalArgumentException e) {
            throw new ConnectorException("Connector returned invalid completed key data", connector);
        }
    }

    private static void validateCreateResponse(ApiClientConnectorInfo connector, CreateKeyRequestV2Dto request,
                                               ResponseEntity<?> response) throws ConnectorException {
        try {
            OperationResponseValidator.validateCreate(request.getExecutionMode(), response);
            if (request.getExecutionMode() == OperationExecutionMode.SYNCHRONOUS)
                validateCompletedResponse(connector, response.getBody());
        } catch (IllegalArgumentException e) {
            throw new ConnectorException(e.getMessage(), connector);
        }
    }
}
