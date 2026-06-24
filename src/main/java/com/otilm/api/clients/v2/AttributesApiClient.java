package com.otilm.api.clients.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.BaseApiClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.interfaces.client.v2.AttributesSyncApiClient;
import com.otilm.api.model.client.connector.v2.attribute.AttributeCallbackRequestDto;
import com.otilm.api.model.client.connector.v2.attribute.AttributeCallbackResponseDto;
import com.otilm.api.model.client.connector.v2.attribute.AttributeDefinitionsDto;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.net.ssl.TrustManager;
import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * WebClient (HTTP) implementation of the Attributes v2 API client (common NG provider interface),
 * mirroring the sibling MQ-based {@link com.otilm.api.clients.mq.v2.AttributesApiClient} and the
 * Info/Health/Metrics v2 trio.
 *
 * <p>The path constants below are part of the v2 connector API contract — they describe the routes a
 * connector implementation must expose, not URIs configurable per environment.</p>
 */
@SuppressWarnings("java:S1075") // contract paths, not configurable URIs
public class AttributesApiClient extends BaseApiClient implements AttributesSyncApiClient {

    private static final String ATTRIBUTES_CONTEXT = "/v2/attributes";
    private static final String ATTRIBUTE_CONTEXT = ATTRIBUTES_CONTEXT + "/{uuid}";
    private static final String CALLBACK_CONTEXT = ATTRIBUTES_CONTEXT + "/callback";

    private static final String UUIDS_QUERY_PARAM = "uuids";

    public AttributesApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        super(webClient, defaultTrustManagers);
    }

    @Override
    public AttributeDefinitionsDto listDefinitions(ApiClientConnectorInfo connector, List<UUID> uuids) throws ConnectorException {
        UriBuilder uriBuilder = UriComponentsBuilder.fromUriString(connector.getUrl()).path(ATTRIBUTES_CONTEXT);
        if (uuids != null && !uuids.isEmpty()) {
            // Exploded form: one repeated `uuids` query parameter per UUID (matches the controller's
            // Explode.TRUE), NOT a single CSV value.
            for (UUID uuid : uuids) {
                uriBuilder.queryParam(UUIDS_QUERY_PARAM, uuid);
            }
        }
        URI uri = uriBuilder.build();

        WebClient.RequestBodySpec request = prepareRequest(HttpMethod.GET, connector, true).uri(uri);
        return processRequest(r -> r
                        .retrieve()
                        .bodyToMono(AttributeDefinitionsDto.class)
                        .block(),
                request,
                connector);
    }

    @Override
    public BaseAttribute getDefinition(ApiClientConnectorInfo connector, UUID uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);
        return processRequest(r -> r
                        .uri(connector.getUrl() + ATTRIBUTE_CONTEXT, uuid)
                        .retrieve()
                        .bodyToMono(BaseAttribute.class)
                        .block(),
                request,
                connector);
    }

    @Override
    public AttributeCallbackResponseDto callback(ApiClientConnectorInfo connector, AttributeCallbackRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        return processRequest(r -> r
                        .uri(connector.getUrl() + CALLBACK_CONTEXT)
                        .body(Mono.just(requestDto), AttributeCallbackRequestDto.class)
                        .retrieve()
                        .bodyToMono(AttributeCallbackResponseDto.class)
                        .block(),
                request,
                connector);
    }
}
