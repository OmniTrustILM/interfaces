package com.otilm.api.clients.cryptography.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.BaseApiClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.interfaces.client.v2.TokenSyncApiClient;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.v2.OperationResponseValidator;
import com.otilm.api.model.connector.cryptography.v2.OperationValidationResult;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenStatusResponseV2Dto;
import com.otilm.api.model.core.cryptography.key.KeyUsage;
import java.util.List;
import javax.net.ssl.TrustManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient implementation of the connector-facing v2 stateless token API.
 */
@Slf4j
@SuppressWarnings("java:S1075") // contract paths, not configurable URIs
public class TokenApiClient extends BaseApiClient implements TokenSyncApiClient {

    private static final String BASE_PATH = "/v2/cryptographyProvider/tokens";
    private static final String KEY_REQUEST_TYPES_PATH = BASE_PATH + "/keyRequestTypes";
    private static final String ATTRIBUTES_PATH = BASE_PATH + "/attributes";
    private static final String STATUS_PATH = BASE_PATH + "/status";
    private static final String PROFILE_ATTRIBUTES_PATH = BASE_PATH + "/tokenProfile/attributes";
    private static final String PROFILE_KEY_USAGES_PATH = BASE_PATH + "/tokenProfile/keyUsages";
    private final OperationResponseValidator responseValidator;

    public TokenApiClient(WebClient webClient, TrustManager[] defaultTrustManagers,
            OperationResponseValidator responseValidator) {
        super(webClient, defaultTrustManagers);
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
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);
        List<BaseAttribute> response = processRequest(r -> requireBody(
                r.uri(connector.getUrl() + ATTRIBUTES_PATH).retrieve().toEntityList(BaseAttribute.class),
                "listTokenAttributes"), request, connector);
        requireValid(responseValidator.validateAttributeList(response), connector);
        return response;
    }

    @Override
    public TokenStatusResponseV2Dto getTokenStatus(ApiClientConnectorInfo connector, TokenScopedRequestV2Dto body)
            throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        TokenStatusResponseV2Dto response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + STATUS_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntity(TokenStatusResponseV2Dto.class), "getTokenStatus"), request, connector);
        requireValid(responseValidator.validateTokenStatus(response), connector);
        return response;
    }

    @Override
    public List<BaseAttribute> listTokenProfileAttributes(ApiClientConnectorInfo connector,
            TokenScopedRequestV2Dto body) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        List<BaseAttribute> response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + PROFILE_ATTRIBUTES_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntityList(BaseAttribute.class), "listTokenProfileAttributes"), request, connector);
        requireValid(responseValidator.validateAttributeList(response), connector);
        return response;
    }

    @Override
    public List<KeyUsage> listTokenProfileKeyUsages(ApiClientConnectorInfo connector, TokenScopedRequestV2Dto body)
            throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        List<KeyUsage> response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + PROFILE_KEY_USAGES_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntityList(KeyUsage.class), "listTokenProfileKeyUsages"), request, connector);
        requireValid(responseValidator.validateKeyUsageList(response), connector);
        return response;
    }

    @Override
    public List<KeyRequestType> listSupportedKeyRequestTypes(ApiClientConnectorInfo connector,
            TokenProfileScopedRequestV2Dto body) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        ResponseEntity<List<KeyRequestType>> response = processRequest(r -> requireResponse(r
                .uri(connector.getUrl() + KEY_REQUEST_TYPES_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<KeyRequestType>>() {
                }), "listSupportedKeyRequestTypes"), request, connector);
        List<KeyRequestType> keyRequestTypes = response.getBody();
        requireValid(responseValidator.validateSupportedKeyRequestTypes(keyRequestTypes), connector);
        return keyRequestTypes;
    }
}
