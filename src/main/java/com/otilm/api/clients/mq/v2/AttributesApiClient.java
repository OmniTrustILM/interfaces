package com.otilm.api.clients.mq.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.mq.ProxyClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.interfaces.client.v2.AttributesSyncApiClient;
import com.otilm.api.model.client.connector.v2.attribute.AttributeCallbackRequestDto;
import com.otilm.api.model.client.connector.v2.attribute.AttributeCallbackResponseDto;
import com.otilm.api.model.client.connector.v2.attribute.AttributeDefinitionsDto;
import com.otilm.api.model.common.attribute.common.BaseAttribute;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * MQ-based implementation of the Attributes v2 API client (common NG provider interface), mirroring the
 * sibling MQ Info client and the HTTP {@link com.otilm.api.clients.v2.AttributesApiClient}.
 *
 * <p><b>AC5 / MQ error parity is intentionally NOT handled here — it is a Core concern.</b> The
 * {@link ProxyClient} implementation maps connector errors by raw HTTP status code and does not
 * reconstruct a {@code ConnectorProblemException} from the connector's {@code errorCode}. So a connector
 * 404 ({@code ATTRIBUTE_DEFINITION_NOT_FOUND}) over MQ surfaces without its error code. Restoring that
 * parity is tracked as a Core gate (core #1622/#1621); #726 ships the transport only.</p>
 *
 * <p>{@code ProxyClient} also has no notion of a query string, so the exploded {@code uuids} filter rides
 * inside the request path string (e.g. {@code /v2/attributes?uuids=a&uuids=b}); whether that survives the
 * bus is a Core/MQ verify-item, not asserted here.</p>
 */
@SuppressWarnings("java:S1075") // contract paths, not configurable URIs
public class AttributesApiClient implements AttributesSyncApiClient {

    private static final String ATTRIBUTES_PATH = "/v2/attributes";
    private static final String CALLBACK_PATH = ATTRIBUTES_PATH + "/callback";

    private static final String UUIDS_QUERY_PARAM = "uuids";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";

    private final ProxyClient proxyClient;

    public AttributesApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public AttributeDefinitionsDto listDefinitions(ApiClientConnectorInfo connector, List<UUID> uuids) throws ConnectorException {
        return proxyClient.sendRequest(connector, listDefinitionsPath(uuids), HTTP_METHOD_GET, null, AttributeDefinitionsDto.class);
    }

    public CompletableFuture<AttributeDefinitionsDto> listDefinitionsAsync(ApiClientConnectorInfo connector, List<UUID> uuids) {
        return proxyClient.sendRequestAsync(connector, listDefinitionsPath(uuids), HTTP_METHOD_GET, null, AttributeDefinitionsDto.class);
    }

    @Override
    public BaseAttribute getDefinition(ApiClientConnectorInfo connector, UUID uuid) throws ConnectorException {
        return proxyClient.sendRequest(connector, definitionPath(uuid), HTTP_METHOD_GET, null, BaseAttribute.class);
    }

    public CompletableFuture<BaseAttribute> getDefinitionAsync(ApiClientConnectorInfo connector, UUID uuid) {
        return proxyClient.sendRequestAsync(connector, definitionPath(uuid), HTTP_METHOD_GET, null, BaseAttribute.class);
    }

    @Override
    public AttributeCallbackResponseDto callback(ApiClientConnectorInfo connector, AttributeCallbackRequestDto request) throws ConnectorException {
        return proxyClient.sendRequest(connector, CALLBACK_PATH, HTTP_METHOD_POST, request, AttributeCallbackResponseDto.class);
    }

    public CompletableFuture<AttributeCallbackResponseDto> callbackAsync(ApiClientConnectorInfo connector, AttributeCallbackRequestDto request) {
        return proxyClient.sendRequestAsync(connector, CALLBACK_PATH, HTTP_METHOD_POST, request, AttributeCallbackResponseDto.class);
    }

    private static String listDefinitionsPath(List<UUID> uuids) {
        if (uuids == null || uuids.isEmpty()) {
            return ATTRIBUTES_PATH;
        }
        // Exploded form (one repeated `uuids` param per UUID), carried in the path since the proxy has
        // no query-string notion. Bus survival of this is a Core/MQ gate, not asserted in #726.
        String query = uuids.stream()
                .map(uuid -> UUIDS_QUERY_PARAM + "=" + uuid)
                .collect(Collectors.joining("&"));
        return ATTRIBUTES_PATH + "?" + query;
    }

    private static String definitionPath(UUID uuid) {
        return ATTRIBUTES_PATH + "/" + uuid;
    }
}
