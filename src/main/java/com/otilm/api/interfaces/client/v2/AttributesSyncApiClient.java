package com.otilm.api.interfaces.client.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.client.connector.v2.attribute.AttributeCallbackRequestDto;
import com.otilm.api.model.client.connector.v2.attribute.AttributeCallbackResponseDto;
import com.otilm.api.model.client.connector.v2.attribute.AttributeDefinitionsDto;
import com.otilm.api.model.common.attribute.common.BaseAttribute;

import java.util.List;
import java.util.UUID;

/**
 * Sync interface for the Attributes v2 API client (the common NG provider interface, alongside
 * {@link InfoSyncApiClient}). Implemented by both the REST and the MQ client.
 *
 * <p>Mirrors the three endpoints of
 * {@code com.otilm.api.interfaces.connector.common.v2.AttributesController}:
 * <ul>
 *   <li>{@code GET /v2/attributes} — list the attribute-definition registry (optionally filtered);</li>
 *   <li>{@code GET /v2/attributes/{uuid}} — fetch a single definition;</li>
 *   <li>{@code POST /v2/attributes/callback} — resolve dynamic attribute content.</li>
 * </ul>
 */
public interface AttributesSyncApiClient {

    /**
     * List attribute definitions. When {@code uuids} is {@code null} or empty, the full registry is
     * returned ({@code GET /v2/attributes}); otherwise only the matching definitions are returned via
     * the exploded query {@code ?uuids=a&uuids=b} (one repeated parameter per UUID, NOT a CSV value).
     */
    AttributeDefinitionsDto listDefinitions(ApiClientConnectorInfo connector, List<UUID> uuids) throws ConnectorException;

    BaseAttribute getDefinition(ApiClientConnectorInfo connector, UUID uuid) throws ConnectorException;

    AttributeCallbackResponseDto callback(ApiClientConnectorInfo connector, AttributeCallbackRequestDto request) throws ConnectorException;
}
