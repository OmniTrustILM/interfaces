package com.czertainly.api.clients.v2;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.clients.BaseApiClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v2.MetricsSyncApiClient;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

import javax.net.ssl.TrustManager;

public class MetricsApiClient extends BaseApiClient implements MetricsSyncApiClient {

    public MetricsApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        super(webClient, defaultTrustManagers);
    }

    @Override
    public String getMetrics(ApiClientConnectorInfo connector) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);
        return processRequest(r -> r
                        .uri(connector.getUrl() + "/v1/metrics")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block(),
                request,
                connector);
    }
}

