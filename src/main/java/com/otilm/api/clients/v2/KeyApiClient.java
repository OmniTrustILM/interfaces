package com.otilm.api.clients.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.BaseApiClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.interfaces.client.v2.KeySyncApiClient;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.cryptography.v2.OperationResponseValidator;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import javax.net.ssl.TrustManager;
import java.util.Arrays;
import java.util.List;

/**
 * WebClient implementation of the connector-facing stateless cryptography v2 key API.
 */
@SuppressWarnings("java:S1075")
public class KeyApiClient extends BaseApiClient implements KeySyncApiClient {

    private static final String BASE_PATH = "/v2/cryptographyProvider/keys";

    public KeyApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        super(webClient, defaultTrustManagers);
    }

    @Override
    public List<KeyRequestType> listSupportedKeyTypes(ApiClientConnectorInfo connector,
                                                      TokenProfileScopedRequestV2Dto body)
            throws ConnectorException {
        List<KeyRequestType> types = Arrays.asList(postArray(
                connector, "/types", body, KeyRequestType[].class, "listSupportedKeyTypes"));
        if (types.stream().anyMatch(java.util.Objects::isNull)) {
            throw new ConnectorException("Connector returned a null supported key type", connector);
        }
        return types;
    }

    @Override
    public List<BaseAttribute> listCreateKeyAttributes(ApiClientConnectorInfo connector,
                                                       KeyRequestType type,
                                                       TokenProfileScopedRequestV2Dto body)
            throws ConnectorException {
        String segment = attributePath(type, connector);
        return postList(connector, segment, body, BaseAttribute.class, "listCreateKeyAttributes");
    }

    @Override
    public ResponseEntity<SecretKeyDataResponseV2Dto> createSecretKey(ApiClientConnectorInfo connector, CreateKeyRequestV2Dto body)
            throws ConnectorException {
        ResponseEntity<SecretKeyDataResponseV2Dto> response = postEntity(connector, "/secret", body,
                SecretKeyDataResponseV2Dto.class, "createSecretKey");
        validateCreateResponse(connector, body, response);
        return response;
    }

    @Override
    public ResponseEntity<KeyPairDataResponseV2Dto> createKeyPair(ApiClientConnectorInfo connector, CreateKeyRequestV2Dto body)
            throws ConnectorException {
        ResponseEntity<KeyPairDataResponseV2Dto> response = postEntity(connector, "/pair", body,
                KeyPairDataResponseV2Dto.class, "createKeyPair");
        validateCreateResponse(connector, body, response);
        return response;
    }

    @Override
    public KeyOperationStatusResponseV2Dto getCreateKeyStatus(ApiClientConnectorInfo c, KeyOperationStatusRequestV2Dto b) throws ConnectorException {
        return postBody(c, "/create/status", b, KeyOperationStatusResponseV2Dto.class, "getCreateKeyStatus");
    }

    @Override
    public ResponseEntity<Void> cancelCreateKey(ApiClientConnectorInfo c, KeyOperationCancelRequestV2Dto b) throws ConnectorException {
        return postVoid(c, "/create/cancel", b, "cancelCreateKey");
    }

    @Override
    public ResponseEntity<KeyDataResponseV2Dto> destroyKey(ApiClientConnectorInfo c, DestroyKeyRequestV2Dto b) throws ConnectorException {
        ResponseEntity<KeyDataResponseV2Dto> r = postEntity(c, "/destroy", b, KeyDataResponseV2Dto.class, "destroyKey");
        try {
            OperationResponseValidator.validateDestroy(b.getExecutionMode(), r);
        } catch (IllegalArgumentException e) {
            throw new ConnectorException(e.getMessage(), c);
        }
        return r;
    }

    @Override
    public KeyOperationStatusResponseV2Dto getDestroyKeyStatus(ApiClientConnectorInfo c, KeyOperationStatusRequestV2Dto b) throws ConnectorException {
        return postBody(c, "/destroy/status", b, KeyOperationStatusResponseV2Dto.class, "getDestroyKeyStatus");
    }

    @Override
    public ResponseEntity<Void> cancelDestroyKey(ApiClientConnectorInfo c, KeyOperationCancelRequestV2Dto b) throws ConnectorException {
        return postVoid(c, "/destroy/cancel", b, "cancelDestroyKey");
    }

    private <T> ResponseEntity<T> postEntity(ApiClientConnectorInfo connector, String path, Object body,
                                             Class<T> responseType, String operation) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        return processRequest(r -> requireResponse(
                        r.uri(connector.getUrl() + BASE_PATH + path)
                                .bodyValue(body)
                                .retrieve()
                                .toEntity(responseType),
                        operation),
                request,
                connector);
    }

    private <T> T postBody(ApiClientConnectorInfo c, String p, Object b, Class<T> t, String o) throws ConnectorException {
        ResponseEntity<T> r = postEntity(c, p, b, t, o);
        if (r.getBody() == null)
            throw new ConnectorException("Key request returned no body: " + o, c);
        return r.getBody();
    }

    private ResponseEntity<Void> postVoid(ApiClientConnectorInfo connector, String path, Object body,
                                          String operation) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        return processRequest(r -> requireResponse(
                        r.uri(connector.getUrl() + BASE_PATH + path)
                                .bodyValue(body)
                                .retrieve()
                                .toBodilessEntity(),
                        operation),
                request,
                connector);
    }

    private <T> List<T> postList(ApiClientConnectorInfo connector, String path, Object body,
                                 Class<T> responseType, String operation) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        return processRequest(r -> requireBody(
                        r.uri(connector.getUrl() + BASE_PATH + path)
                                .bodyValue(body)
                                .retrieve()
                                .toEntityList(responseType),
                        operation),
                request,
                connector);
    }

    private <T> T postArray(ApiClientConnectorInfo connector, String path, Object body,
                            Class<T> responseType, String operation) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        return processRequest(r -> requireBody(
                        r.uri(connector.getUrl() + BASE_PATH + path)
                                .bodyValue(body)
                                .retrieve()
                                .toEntity(responseType),
                        operation),
                request,
                connector);
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
            if (request.getExecutionMode() == OperationExecutionMode.SYNCHRONOUS) {
                validateCompletedResponse(connector, response.getBody());
            }
        } catch (IllegalArgumentException e) {
            throw new ConnectorException(e.getMessage(), connector);
        }
    }
}
