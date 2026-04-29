package com.otilm.api.clients.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.BaseApiClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.interfaces.client.v2.InfoSyncApiClient;
import com.otilm.api.model.client.connector.v2.InfoResponse;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

import javax.net.ssl.TrustManager;

public class InfoApiClient extends BaseApiClient implements InfoSyncApiClient {

    public InfoApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        super(webClient, defaultTrustManagers);
    }

    @Override
    public InfoResponse getConnectorInfo(ApiClientConnectorInfo connector) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);
        return processRequest(r -> r
                        .uri(connector.getUrl() + "/v2/info")
                        .retrieve()
                        .bodyToMono(InfoResponse.class)
                        .block(),
                request,
                connector);
    }
}
