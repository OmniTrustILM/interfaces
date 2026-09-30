package com.otilm.api.interfaces.client.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.discovery.v2.DiscoveryDrainRequestDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryInitiateRequestDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryInitiateResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryResultsResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryRunRequestDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryStatusResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryStopResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoverySupportedResourceDto;
import com.otilm.api.model.core.auth.Resource;
import java.util.List;
import org.springframework.http.ResponseEntity;

/**
 * Sync interface for v2 Discovery API client operations. Implemented by both REST and MQ clients.
 *
 * <p>
 * Return-type convention:
 * <ul>
 * <li>{@code ResponseEntity<Void>} for cancel — the caller reads {@code 204} (cancelled) versus {@code 404} (the
 * connector no longer tracks the run, the terminal state cancel asked for, so it counts as success). Every other
 * failure is thrown, including the {@code 422} refusal for a run past the point of no return.</li>
 * <li>plain DTOs for initiate and resume — both always {@code 202 Accepted}, so there is no sync-versus-async ambiguity
 * to resolve (unlike the v3 certificate client).</li>
 * <li>plain DTOs for all other operations — listing, status queries, results drainage, stop.</li>
 * </ul>
 *
 * <p>
 * <b>Thrown failures are transport-dependent.</b> Over REST a connector's RFC 9457 body becomes a
 * {@code ConnectorProblemException} carrying its {@code ErrorCode}, so a caller can distinguish {@code CHECKPOINT_LOST}
 * from any other 410. Over MQ the proxy classifies by HTTP status alone and discards the problem body, so no
 * {@code ErrorCode} is available and a {@code 422} arrives as the unchecked {@code ValidationException}, uncovered by
 * the declared {@code ConnectorException}. Never assume an {@code ErrorCode} is present.
 *
 * <p>
 * The list-returning operations hand back a mutable list on both transports, so a caller may sort or filter the result
 * in place.
 *
 * <p>
 * A 2xx carrying no body at all is non-conformant and both transports reject it, but not with the same type: MQ raises
 * a connector-attributed {@code ConnectorException} while REST surfaces the shared client's
 * {@code IllegalStateException}, which is unchecked and so outside the declared throws. Making that uniform means
 * retyping {@code BaseApiClient.processRequest}'s functional parameter, which is public API every connector compiles
 * against, so it is stated here instead.
 */
public interface DiscoverySyncApiClient {

    List<DiscoverySupportedResourceDto> listSupportedResources(ApiClientConnectorInfo connector)
            throws ConnectorException;

    List<BaseAttribute> listRunAttributes(ApiClientConnectorInfo connector) throws ConnectorException;

    /**
     * @param resource must be discoverable — {@link Resource#CERTIFICATE} or {@link Resource#CRYPTOGRAPHIC_KEY}, the
     * two the contract defines payloads for. Both transports reject anything else with
     * {@link IllegalArgumentException}.
     */
    List<BaseAttribute> listResourceAttributes(ApiClientConnectorInfo connector, Resource resource)
            throws ConnectorException;

    DiscoveryInitiateResponseDto initiate(ApiClientConnectorInfo connector, DiscoveryInitiateRequestDto request)
            throws ConnectorException;

    DiscoveryStatusResponseDto status(ApiClientConnectorInfo connector, DiscoveryRunRequestDto request)
            throws ConnectorException;

    DiscoveryResultsResponseDto results(ApiClientConnectorInfo connector, DiscoveryDrainRequestDto request)
            throws ConnectorException;

    DiscoveryStopResponseDto stop(ApiClientConnectorInfo connector, DiscoveryRunRequestDto request)
            throws ConnectorException;

    DiscoveryInitiateResponseDto resume(ApiClientConnectorInfo connector, DiscoveryRunRequestDto request)
            throws ConnectorException;

    ResponseEntity<Void> cancel(ApiClientConnectorInfo connector, DiscoveryRunRequestDto request)
            throws ConnectorException;

}
