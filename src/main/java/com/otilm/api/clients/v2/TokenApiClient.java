package com.otilm.api.clients.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.BaseApiClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.interfaces.client.v2.TokenSyncApiClient;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.v2.token.TokenScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenStatusResponseV2Dto;
import com.otilm.api.model.core.cryptography.key.KeyUsage;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.net.ssl.TrustManager;
import java.util.List;

/**
 * WebClient implementation of the connector-facing v2 stateless token API.
 */
@SuppressWarnings("java:S1075")
public class TokenApiClient extends BaseApiClient implements TokenSyncApiClient {

    private static final String BASE_PATH = "/v2/cryptographyProvider/tokens";
    private static final String ATTRIBUTES_PATH = BASE_PATH + "/attributes";
    private static final String STATUS_PATH = BASE_PATH + "/status";
    private static final String PROFILE_ATTRIBUTES_PATH = BASE_PATH + "/tokenProfile/attributes";
    private static final String PROFILE_KEY_USAGES_PATH = BASE_PATH + "/tokenProfile/keyUsages";

    public TokenApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        this.webClient = webClient;
        this.defaultTrustManagers = defaultTrustManagers;
    }

    @Override
    public List<BaseAttribute> listTokenAttributes(ApiClientConnectorInfo connector) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);
        return processRequest(r -> requireBody(
                        r.uri(connector.getUrl() + ATTRIBUTES_PATH)
                                .retrieve()
                                .toEntityList(BaseAttribute.class),
                        "listTokenAttributes"),
                request,
                connector);
    }

    @Override
    public TokenStatusResponseV2Dto getTokenStatus(ApiClientConnectorInfo connector,
                                                   TokenScopedRequestV2Dto body) throws ConnectorException {
        return post(connector, STATUS_PATH, body, TokenStatusResponseV2Dto.class, "getTokenStatus");
    }

    @Override
    public List<BaseAttribute> listTokenProfileAttributes(ApiClientConnectorInfo connector,
                                                          TokenScopedRequestV2Dto body) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        return processRequest(r -> requireBody(
                        r.uri(connector.getUrl() + PROFILE_ATTRIBUTES_PATH)
                                .body(Mono.just(body), TokenScopedRequestV2Dto.class)
                                .retrieve()
                                .toEntityList(BaseAttribute.class),
                        "listTokenProfileAttributes"),
                request,
                connector);
    }

    @Override
    public List<KeyUsage> listTokenProfileKeyUsages(ApiClientConnectorInfo connector,
                                                    TokenScopedRequestV2Dto body) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        return processRequest(r -> requireBody(
                        r.uri(connector.getUrl() + PROFILE_KEY_USAGES_PATH)
                                .body(Mono.just(body), TokenScopedRequestV2Dto.class)
                                .retrieve()
                                .toEntityList(KeyUsage.class),
                        "listTokenProfileKeyUsages"),
                request,
                connector);
    }

    private <T> T post(ApiClientConnectorInfo connector, String path, Object body,
                       Class<T> responseType, String operation) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        return processRequest(r -> requireBody(
                        r.uri(connector.getUrl() + path)
                                .bodyValue(body)
                                .retrieve()
                                .toEntity(responseType),
                        operation),
                request,
                connector);
    }
}
