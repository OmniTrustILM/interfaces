package com.czertainly.api.clients.v2;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.clients.BaseApiClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v2.HealthSyncApiClient;
import com.czertainly.api.model.client.connector.v2.HealthInfo;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

import javax.net.ssl.TrustManager;

public class HealthApiClient extends BaseApiClient implements HealthSyncApiClient {

    public HealthApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        super(webClient, defaultTrustManagers);
    }

    @Override
    public HealthInfo checkHealth(ApiClientConnectorInfo connector) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);
        return processRequest(r -> r
                        .uri(connector.getUrl() + "/v2/health")
                        .retrieve()
                        .bodyToMono(HealthInfo.class)
                        .block(),
                request,
                connector);
    }

    @Override
    public HealthInfo checkHealthLiveness(ApiClientConnectorInfo connector) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);
        return processRequest(r -> r
                        .uri(connector.getUrl() + "/v2/health/liveness")
                        .retrieve()
                        .bodyToMono(HealthInfo.class)
                        .block(),
                request,
                connector);
    }

    @Override
    public HealthInfo checkHealthReadiness(ApiClientConnectorInfo connector) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);
        return processRequest(r -> r
                        .uri(connector.getUrl() + "/v2/health/readiness")
                        .retrieve()
                        .bodyToMono(HealthInfo.class)
                        .block(),
                request,
                connector);
    }
}
