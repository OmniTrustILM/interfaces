package com.otilm.api.clients.mq.discovery.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.discovery.v2.DiscoveryPaths;
import com.otilm.api.clients.mq.ProxyClient;
import com.otilm.api.exception.ConnectorClientException;
import com.otilm.api.exception.ConnectorEntityNotFoundException;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.ConnectorProblemException;
import com.otilm.api.exception.ConnectorServerException;
import com.otilm.api.interfaces.client.v2.DiscoverySyncApiClient;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.error.ConnectorOperationErrorCodes;
import com.otilm.api.model.connector.discovery.v2.DiscoveryDrainRequestDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryInitiateRequestDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryInitiateResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryResultsResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryRunRequestDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryStatusResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryStopResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoverySupportedResourceDto;
import com.otilm.api.model.core.auth.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * MQ-based implementation of the v2 Discovery API client.
 *
 * <p>
 * Every call passes an explicit {@code Duration} sized by {@link DiscoveryMqTimeouts} — this client never invokes a
 * {@link ProxyClient} overload that falls back to the proxy's default timeout, since a discovery drain can legitimately
 * take far longer than a status poll.
 *
 * <p>
 * The proxy hands back {@code null} for a relayed response that carried no body, so every operation guards it and fails
 * naming the operation rather than passing {@code null} on to Core, where it would resurface as a
 * {@link NullPointerException} far from its cause. A list route answering with no body is non-conformant, not empty.
 * Lists that do arrive are mutable, matching the Jackson list REST returns.
 */
@SuppressWarnings("java:S1075") // contract paths, not configurable URIs
public class DiscoveryApiClient implements DiscoverySyncApiClient {

    private static final Logger logger = LoggerFactory.getLogger(DiscoveryApiClient.class);

    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";

    private final ProxyClient proxyClient;
    private final DiscoveryMqTimeouts timeouts;

    /**
     * Both collaborators are validated eagerly, so misconfigured wiring fails at bean construction instead of on the
     * first connector call.
     */
    public DiscoveryApiClient(ProxyClient proxyClient, DiscoveryMqTimeouts timeouts) {
        this.proxyClient = Objects.requireNonNull(proxyClient, "proxyClient is required");
        this.timeouts = Objects.requireNonNull(timeouts, "timeouts is required");
    }

    /** Convenience constructor using {@link DiscoveryMqTimeouts#defaults()}. */
    public DiscoveryApiClient(ProxyClient proxyClient) {
        this(proxyClient, DiscoveryMqTimeouts.defaults());
    }

    @Override
    public List<DiscoverySupportedResourceDto> listSupportedResources(ApiClientConnectorInfo connector)
            throws ConnectorException {
        DiscoverySupportedResourceDto[] result = proxyClient
                .sendRequest(connector, DiscoveryPaths.RESOURCES, HTTP_METHOD_GET, null,
                        DiscoverySupportedResourceDto[].class, timeouts.control());
        return toMutableList(result, "listSupportedResources", connector);
    }

    @Override
    public List<BaseAttribute> listRunAttributes(ApiClientConnectorInfo connector) throws ConnectorException {
        BaseAttribute[] result = proxyClient
                .sendRequest(connector, DiscoveryPaths.ATTRIBUTES, HTTP_METHOD_GET, null, BaseAttribute[].class,
                        timeouts.control());
        return toMutableList(result, "listRunAttributes", connector);
    }

    /**
     * {@code resource} binds to the path by its wire code ({@code "certificates"}, {@code "keys"}), never the Java enum
     * name — {@link Resource#getCode()}.
     */
    @Override
    public List<BaseAttribute> listResourceAttributes(ApiClientConnectorInfo connector, Resource resource)
            throws ConnectorException {
        String path = DiscoveryPaths.resourceAttributes(resource);
        BaseAttribute[] result = proxyClient
                .sendRequest(connector, path, HTTP_METHOD_GET, null, BaseAttribute[].class, timeouts.control());
        return toMutableList(result, "listResourceAttributes", connector);
    }

    @Override
    public DiscoveryInitiateResponseDto initiate(ApiClientConnectorInfo connector, DiscoveryInitiateRequestDto request)
            throws ConnectorException {
        return requireBody(proxyClient
                .sendRequest(connector, DiscoveryPaths.INITIATE, HTTP_METHOD_POST, request,
                        DiscoveryInitiateResponseDto.class, timeouts.control()),
                "initiate", connector);
    }

    @Override
    public DiscoveryStatusResponseDto status(ApiClientConnectorInfo connector, DiscoveryRunRequestDto request)
            throws ConnectorException {
        return requireBody(proxyClient
                .sendRequest(connector, DiscoveryPaths.STATUS, HTTP_METHOD_POST, request,
                        DiscoveryStatusResponseDto.class, timeouts.status()),
                "status", connector);
    }

    @Override
    public DiscoveryResultsResponseDto results(ApiClientConnectorInfo connector, DiscoveryDrainRequestDto request)
            throws ConnectorException {
        return requireBody(proxyClient
                .sendRequest(connector, DiscoveryPaths.RESULTS, HTTP_METHOD_POST, request,
                        DiscoveryResultsResponseDto.class, timeouts.drain()),
                "results", connector);
    }

    @Override
    public DiscoveryStopResponseDto stop(ApiClientConnectorInfo connector, DiscoveryRunRequestDto request)
            throws ConnectorException {
        return requireBody(proxyClient
                .sendRequest(connector, DiscoveryPaths.STOP, HTTP_METHOD_POST, request, DiscoveryStopResponseDto.class,
                        timeouts.control()),
                "stop", connector);
    }

    @Override
    public DiscoveryInitiateResponseDto resume(ApiClientConnectorInfo connector, DiscoveryRunRequestDto request)
            throws ConnectorException {
        return requireBody(proxyClient
                .sendRequest(connector, DiscoveryPaths.RESUME, HTTP_METHOD_POST, request,
                        DiscoveryInitiateResponseDto.class, timeouts.control()),
                "resume", connector);
    }

    /**
     * Uses
     * {@link ProxyClient#sendRequestForEntity(ApiClientConnectorInfo, String, String, Object, Class, java.time.Duration)}
     * rather than {@code sendRequest} because the caller needs the status, not the (absent) body.
     *
     * <p>
     * A 404 means the connector no longer tracks the run — the terminal state cancel was asking for, so it reads as an
     * already-terminal cancellation rather than an error. The proxy classifies that 404 into a thrown
     * {@link ConnectorEntityNotFoundException}, and would raise a {@link ConnectorProblemException} once it relays
     * {@code application/problem+json}; both shapes are caught here (the problem one by error code, via
     * {@link ConnectorOperationErrorCodes#isOperationNotTracked}) and returned as a {@code 404} response, exactly as
     * the REST client does — otherwise the identical call would succeed on REST and hard-fail on MQ. Every other
     * failure, including the {@code 422} past-the-point-of-no-return refusal, propagates untouched.
     *
     * <p>
     * Any successful status the proxy reports is normalized to {@code 204}, cancel's only contract success status. The
     * {@code Duration} overload of {@code sendRequestForEntity} is a {@code default} method that wraps the body in
     * {@code ResponseEntity.ok}, so without this an MQ cancel would answer {@code 200} where the controller declares
     * {@code 204}, and a Core adapter branching on exact status values would reject it. Normalizing here keeps both
     * transports in agreement whether or not Core overrides that overload.
     */
    @Override
    public ResponseEntity<Void> cancel(ApiClientConnectorInfo connector, DiscoveryRunRequestDto request)
            throws ConnectorException {
        ResponseEntity<Void> response;
        try {
            response = proxyClient
                    .sendRequestForEntity(connector, DiscoveryPaths.CANCEL, HTTP_METHOD_POST, request, Void.class,
                            timeouts.control());
        } catch (ConnectorEntityNotFoundException | ConnectorProblemException e) {
            if (!isRunNotTracked(e, connector)) {
                throw e;
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).<Void>build();
        }
        if (response == null) {
            throw new ConnectorException("No response received from connector for cancel", connector);
        }
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.noContent().build();
        }
        // DiscoverySyncApiClient allows exactly two returned outcomes, 204 and 404, and requires every
        // other failure to be thrown. A ProxyClient that preserves the upstream status can hand back a
        // 422 or 5xx entity rather than throwing, so returning whatever arrived would break that rule
        // from the inside — including cancel's own 422, which means the run is past the point of no
        // return and must never read as a completed cancellation.
        if (HttpStatus.NOT_FOUND.equals(response.getStatusCode())) {
            // A relayed 404 entity never passes through isRunNotTracked, so it would otherwise reach the
            // caller as a terminal cancellation with none of the warning that path emits. Same ambiguity,
            // same signal: an unimplemented cancel endpoint answers 404 too.
            warnAmbiguous404(connector);
            return response;
        }
        int statusCode = response.getStatusCode().value();
        String message = "Connector %s answered cancel with %d".formatted(connector.getName(), statusCode);
        if (statusCode >= 400 && statusCode < 500) {
            ConnectorClientException clientFailure = new ConnectorClientException(message,
                    HttpStatus.resolve(statusCode) == null ? HttpStatus.BAD_REQUEST : HttpStatus.resolve(statusCode));
            clientFailure.setConnector(connector);
            throw clientFailure;
        }
        ConnectorServerException serverFailure = new ConnectorServerException(message,
                HttpStatus.resolve(statusCode) == null ? HttpStatus.BAD_GATEWAY : HttpStatus.resolve(statusCode));
        serverFailure.setConnector(connector);
        throw serverFailure;
    }

    /**
     * A bare 404 with no not-tracked error code is ambiguous: a connector that never implemented cancel answers 404
     * too, so trusting it as an already-terminal run can report an abort that never happened. Both the thrown and the
     * relayed path emit this, and the REST client emits the same line, so the signal does not depend on which transport
     * or which shape delivered the 404.
     */
    private static void warnAmbiguous404(ApiClientConnectorInfo connector) {
        logger
                .warn("Connector {} answered {} with a 404 carrying no not-tracked error code;"
                        + " treating the run as already terminal on weaker evidence.", connector.getName(),
                        DiscoveryPaths.CANCEL);
    }

    private static boolean isRunNotTracked(ConnectorException ex, ApiClientConnectorInfo connector) {
        if (ex instanceof ConnectorEntityNotFoundException) {
            warnAmbiguous404(connector);
            return true;
        }
        // Status gates the code: a not-tracked code is only legitimate on a 404. Cancel's own 422
        // (past the point of no return) must reach the caller, and REGISTRATION_NOT_FOUND, which the
        // shared predicate accepts as authority's flavour of not-tracked, is itself declared 422.
        // The int, not getHttpStatus(): that calls HttpStatus.valueOf, which throws for a valid code
        // with no enum constant such as 499, so asking it would swap the connector's problem exception
        // for an IllegalArgumentException.
        return ex instanceof ConnectorProblemException cpe
                && cpe.getProblemDetail().getStatus() == HttpStatus.NOT_FOUND.value()
                && ConnectorOperationErrorCodes.isOperationNotTracked(cpe.getProblemDetail().getErrorCode());
    }

    /**
     * An absent body is a non-conformant response, not "no items": a list route must return a JSON array, and reading a
     * missing body as empty would make a broken connector indistinguishable from one that genuinely reports nothing —
     * for {@code listSupportedResources} a distinction Core acts on. REST rejects it identically via
     * {@code BaseApiClient.requireBody}.
     *
     * <p>
     * The list is a fresh {@link ArrayList} rather than an {@link Arrays#asList} view because REST hands back a mutable
     * Jackson list, so in-place sorting or filtering behaves the same on both transports.
     */
    private static <T> List<T> toMutableList(T[] result, String operation, ApiClientConnectorInfo connector)
            throws ConnectorException {
        return new ArrayList<>(Arrays.asList(requireBody(result, operation, connector)));
    }

    /**
     * The MQ counterpart of {@code BaseApiClient.requireBody}: for an operation whose 2xx response must carry a
     * payload, a bodiless response is a connector contract violation, so it fails here naming the operation instead of
     * returning the proxy's {@code null}. Unlike the REST side's {@link IllegalStateException} this uses the declared
     * {@link ConnectorException} channel and attributes the failure to the connector.
     */
    private static <T> T requireBody(T body, String operation, ApiClientConnectorInfo connector)
            throws ConnectorException {
        if (body == null) {
            throw new ConnectorException("Connector returned an empty body for " + operation, connector);
        }
        return body;
    }
}
