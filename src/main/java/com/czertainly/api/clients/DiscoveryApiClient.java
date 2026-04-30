package com.czertainly.api.clients;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v1.DiscoverySyncApiClient;
import com.czertainly.api.model.connector.discovery.DiscoveryDataRequestDto;
import com.czertainly.api.model.connector.discovery.DiscoveryProviderDto;
import com.czertainly.api.model.connector.discovery.DiscoveryRequestDto;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.net.ssl.TrustManager;

public class DiscoveryApiClient extends BaseApiClient implements DiscoverySyncApiClient {

    private static final String DISCOVERY_BASE_CONTEXT = "/v1/discoveryProvider/discover";
    private static final String DISCOVERY_GET_CONTEXT = DISCOVERY_BASE_CONTEXT + "/{uuid}";

    public DiscoveryApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        this.webClient = webClient;
        this.defaultTrustManagers = defaultTrustManagers;
    }


    @Override
    public DiscoveryProviderDto discoverCertificates(ApiClientConnectorInfo connector, DiscoveryRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + DISCOVERY_BASE_CONTEXT)
                .body(Mono.just(requestDto), DiscoveryRequestDto.class)
                .retrieve()
                .toEntity(DiscoveryProviderDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public DiscoveryProviderDto getDiscoveryData(ApiClientConnectorInfo connector, DiscoveryDataRequestDto requestDto, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                        .uri(connector.getUrl() + DISCOVERY_GET_CONTEXT, uuid)
                        .body(Mono.just(requestDto), DiscoveryDataRequestDto.class)
                        .retrieve()
                        .toEntity(DiscoveryProviderDto.class)
                        .block().getBody(),
                request,
                connector);
    }

    @Override
    public void removeDiscovery(ApiClientConnectorInfo connector, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.DELETE, connector, true);

        processRequest(r -> r
                        .uri(connector.getUrl() + DISCOVERY_GET_CONTEXT, uuid)
                        .retrieve()
                        .toEntity(Void.class)
                        .block().getBody(),
                request,
                connector);
    }
}
