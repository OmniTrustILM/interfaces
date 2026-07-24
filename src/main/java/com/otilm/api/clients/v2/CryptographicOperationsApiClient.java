package com.otilm.api.clients.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.BaseApiClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.interfaces.client.v2.CryptographicOperationsSyncApiClient;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.OperationResponseValidator;
import com.otilm.api.model.connector.cryptography.v2.operations.SignDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignOperationScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignOperationStatusResponseV2Dto;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import javax.net.ssl.TrustManager;
import java.util.List;

/**
 * WebClient implementation of the connector-facing stateless cryptography v2 operations API.
 */
@SuppressWarnings("java:S1075")
public class CryptographicOperationsApiClient extends BaseApiClient
        implements CryptographicOperationsSyncApiClient {

    private static final String BASE_PATH = "/v2/cryptographyProvider/operations";

    public CryptographicOperationsApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        super(webClient, defaultTrustManagers);
    }

    @Override
    public List<BaseAttribute> listSignAttributes(ApiClientConnectorInfo connector,
                                                  KeyScopedRequestV2Dto body)
            throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        return processRequest(r -> requireBody(
                        r.uri(connector.getUrl() + BASE_PATH + "/sign/attributes")
                                .bodyValue(body)
                                .retrieve()
                                .toEntityList(BaseAttribute.class),
                        "listSignAttributes"),
                request,
                connector);
    }

    @Override
    public ResponseEntity<SignDataResponseV2Dto> signData(ApiClientConnectorInfo connector, SignDataRequestV2Dto body)
            throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        ResponseEntity<SignDataResponseV2Dto> response = processRequest(r -> requireResponse(
                        r.uri(connector.getUrl() + BASE_PATH + "/sign")
                                .bodyValue(body)
                                .retrieve()
                                .toEntity(SignDataResponseV2Dto.class),
                        "signData"),
                request,
                connector);
        try {
            OperationResponseValidator.validateSign(body.getExecutionMode(), response);
        } catch (IllegalArgumentException e) {
            throw new ConnectorException(e.getMessage(), e, connector);
        }
        return response;
    }

    @Override
    public SignOperationStatusResponseV2Dto getSignStatus(ApiClientConnectorInfo connector,
                                                          SignOperationScopedRequestV2Dto body) throws ConnectorException {
        return postBody(connector, "/sign/status", body, SignOperationStatusResponseV2Dto.class, "getSignStatus");
    }

    @Override
    public ResponseEntity<Void> cancelSign(ApiClientConnectorInfo connector, SignOperationScopedRequestV2Dto body)
            throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        return processRequest(r -> requireResponse(
                        r.uri(connector.getUrl() + BASE_PATH + "/sign/cancel")
                                .bodyValue(body)
                                .retrieve()
                                .toBodilessEntity(),
                        "cancelSign"),
                request,
                connector);
    }

    private <T> T postBody(ApiClientConnectorInfo connector, String path, Object body, Class<T> type,
                           String operation) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        return processRequest(r -> requireBody(
                        r.uri(connector.getUrl() + BASE_PATH + path)
                                .bodyValue(body)
                                .retrieve()
                                .toEntity(type),
                        operation),
                request,
                connector);
    }
}
