package com.otilm.api.clients;

import com.otilm.api.interfaces.client.v1.ConnectorSyncApiClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.client.connector.InfoResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

import javax.net.ssl.TrustManager;
import java.util.List;

public class ConnectorApiClient extends BaseApiClient implements ConnectorSyncApiClient {

    private static final String CONNECTOR_BASE_CONTEXT = "/v1";

    private static final ParameterizedTypeReference<List<InfoResponse>> MAP_TYPE_REF = new ParameterizedTypeReference<>() {
    };

    public ConnectorApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        this.webClient = webClient;
        this.defaultTrustManagers = defaultTrustManagers;
    }

    @Override
    public List<InfoResponse> listSupportedFunctions(ApiClientConnectorInfo connector) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, false);

        return processRequest(r -> r
                        .uri(connector.getUrl() + CONNECTOR_BASE_CONTEXT)
                        .retrieve()
                        .toEntity(MAP_TYPE_REF)
                        .block().getBody(),
                request,
                connector);
    }
}
