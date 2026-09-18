package com.otilm.api.clients.discovery.v2;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.otilm.api.clients.ApiClientCodecs;
import com.otilm.api.clients.BaseApiClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.ConnectorProblemException;
import com.otilm.api.exception.ConnectorServerException;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.error.ErrorCode;
import com.otilm.api.model.connector.discovery.v2.DiscoveryDrainRequestDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryInitiateRequestDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryInitiateResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryResultsResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryRunRequestDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryStatusResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryStopResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoverySupportedResourceDto;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.model.core.connector.ConnectorStatus;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

class DiscoveryApiClientTest {

    private static final UUID RUN_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    /** Named so the swallowed-404 WARN can be asserted to identify the connector it came from. */
    private static final String CONNECTOR_NAME = "discovery-connector-under-test";

    private static final String CANCEL_PATH = "/v2/discoveryProvider/discoveries/cancel";

    private DiscoveryApiClient client;
    private ConnectorDto connector;
    private WireMockServer mockServer;

    @BeforeEach
    void setUp() {
        client = new DiscoveryApiClient(BaseApiClient.prepareWebClient(), null);

        mockServer = new WireMockServer(options().dynamicPort());
        mockServer.start();
        WireMock.configureFor("localhost", mockServer.port());

        connector = new ConnectorDto();
        connector.setName(CONNECTOR_NAME);
        connector.setUrl("http://localhost:" + mockServer.port());
        connector.setStatus(ConnectorStatus.CONNECTED);
    }

    @AfterEach
    void tearDown() {
        mockServer.stop();
    }

    @Test
    void listSupportedResources_returnsResources() throws ConnectorException {
        String json = """
                [
                  { "resource": "certificates" },
                  { "resource": "keys", "capabilities": [] }
                ]
                """;
        mockServer
                .stubFor(WireMock
                        .get("/v2/discoveryProvider/resources")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(json)));

        List<DiscoverySupportedResourceDto> result = client.listSupportedResources(connector);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(Resource.CERTIFICATE, result.get(0).getResource());
        // Entry 1 still carries the retired per-resource "capabilities" field: a connector built against
        // the earlier contract draft must keep connecting, so the reader tolerates and ignores it.
        Assertions.assertEquals(Resource.CRYPTOGRAPHIC_KEY, result.get(1).getResource());
    }

    /**
     * The list operations decode an array and wrap it, so the wrapper — not Jackson or Reactor — decides mutability.
     * Both transports must hand back a list a caller can sort or filter in place.
     */
    @Test
    void listSupportedResources_returnsMutableList() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .get("/v2/discoveryProvider/resources")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("[{\"resource\":\"certificates\"}]")));

        List<DiscoverySupportedResourceDto> result = client.listSupportedResources(connector);

        Assertions.assertDoesNotThrow(() -> result.add(new DiscoverySupportedResourceDto()));
        Assertions.assertEquals(2, result.size());
    }

    @Test
    void listRunAttributes_returnsAttributes() throws ConnectorException {
        String json = """
                [
                  { "uuid": "11111111-1111-1111-1111-111111111111", "name": "targetAddress", "type": "data", "contentType": "string", "version": 2 }
                ]
                """;
        mockServer
                .stubFor(WireMock
                        .get("/v2/discoveryProvider/attributes")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(json)));

        List<BaseAttribute> result = client.listRunAttributes(connector);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("targetAddress", result.get(0).getName());
    }

    /**
     * The path segment is the resource's wire code ({@code "keys"}), never the Java enum name
     * ({@code CRYPTOGRAPHIC_KEY}) — verified by pinning the stub to the literal path.
     */
    @Test
    void listResourceAttributes_usesResourceWireCodeInPath() throws ConnectorException {
        String json = """
                [
                  { "uuid": "22222222-2222-2222-2222-222222222222", "name": "keySize", "type": "data", "contentType": "integer", "version": 2 }
                ]
                """;
        mockServer
                .stubFor(WireMock
                        .get("/v2/discoveryProvider/keys/attributes")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(json)));

        List<BaseAttribute> result = client.listResourceAttributes(connector, Resource.CRYPTOGRAPHIC_KEY);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("keySize", result.get(0).getName());
    }

    /**
     * Body matchers, not just path and method: without them a client that dropped {@code .body(...)}, sent the wrong
     * DTO, or lost {@code runId} would still match the stub and pass. The {@code resources} entry pins the wire code
     * {@code "certificates"} — the request-body half of the wire-code contract
     * {@code listResourceAttributes_usesResourceWireCodeInPath} pins on the path.
     */
    @Test
    void initiate_returnsMeta() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .post("/v2/discoveryProvider/discoveries/initiate")
                        .withRequestBody(WireMock.matchingJsonPath("$.runId", WireMock.equalTo(RUN_ID.toString())))
                        .withRequestBody(WireMock.matchingJsonPath("$.resources[0]", WireMock.equalTo("certificates")))
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(202)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("{\"checkpoint\":[],\"stoppable\":true}")));

        DiscoveryInitiateRequestDto request = new DiscoveryInitiateRequestDto();
        request.setRunId(RUN_ID);
        request.setResources(List.of(Resource.CERTIFICATE));

        DiscoveryInitiateResponseDto response = client.initiate(connector, request);

        Assertions.assertNotNull(response.getCheckpoint());
        Assertions.assertTrue(response.getCheckpoint().isEmpty());
        Assertions.assertEquals(Boolean.TRUE, response.getStoppable());
    }

    /** Pins the run-request body shape (the DTO shared by status, stop, resume and cancel). */
    @Test
    void status_returnsRunState() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .post("/v2/discoveryProvider/discoveries/status")
                        .withRequestBody(WireMock.matchingJsonPath("$.runId", WireMock.equalTo(RUN_ID.toString())))
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("{\"state\":\"running\",\"highestSequence\":42}")));

        DiscoveryRunRequestDto request = new DiscoveryRunRequestDto();
        request.setRunId(RUN_ID);

        DiscoveryStatusResponseDto response = client.status(connector, request);

        Assertions.assertEquals("running", response.getState().getCode());
        Assertions.assertEquals(42L, response.getHighestSequence());
    }

    /**
     * Pins the drain body shape, including the {@code afterSequence} cursor — a drain that silently lost its cursor
     * would re-read the run from the start on every poll, which no response assertion would notice.
     */
    @Test
    void results_returnsPage() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .post("/v2/discoveryProvider/discoveries/results")
                        .withRequestBody(WireMock.matchingJsonPath("$.runId", WireMock.equalTo(RUN_ID.toString())))
                        .withRequestBody(WireMock.matchingJsonPath("$.afterSequence", WireMock.equalTo("12")))
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("{\"items\":[],\"highestSequence\":7,\"more\":false}")));

        DiscoveryDrainRequestDto request = new DiscoveryDrainRequestDto();
        request.setRunId(RUN_ID);
        request.setAfterSequence(12L);

        DiscoveryResultsResponseDto response = client.results(connector, request);

        Assertions.assertTrue(response.getItems().isEmpty());
        Assertions.assertEquals(7L, response.getHighestSequence());
        Assertions.assertEquals(Boolean.FALSE, response.getMore());
    }

    @Test
    void stop_returnsMeta() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .post("/v2/discoveryProvider/discoveries/stop")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("{\"checkpoint\":[]}")));

        DiscoveryRunRequestDto request = new DiscoveryRunRequestDto();
        request.setRunId(RUN_ID);

        DiscoveryStopResponseDto response = client.stop(connector, request);

        Assertions.assertNotNull(response.getCheckpoint());
        Assertions.assertTrue(response.getCheckpoint().isEmpty());
    }

    @Test
    void resume_returnsMeta() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .post("/v2/discoveryProvider/discoveries/resume")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(202)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("{\"checkpoint\":[]}")));

        DiscoveryRunRequestDto request = new DiscoveryRunRequestDto();
        request.setRunId(RUN_ID);

        DiscoveryInitiateResponseDto response = client.resume(connector, request);

        Assertions.assertNotNull(response.getCheckpoint());
        Assertions.assertTrue(response.getCheckpoint().isEmpty());
        // The stub omits stoppable: absent must decode as null (undeclared), never a defaulted false.
        Assertions.assertNull(response.getStoppable());
    }

    @Test
    void cancel_204OnSuccess() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .post("/v2/discoveryProvider/discoveries/cancel")
                        .willReturn(WireMock.aResponse().withStatus(204)));

        DiscoveryRunRequestDto request = new DiscoveryRunRequestDto();
        request.setRunId(RUN_ID);

        ResponseEntity<Void> response = client.cancel(connector, request);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    /**
     * A 404 on cancel means the run is already terminal, i.e. success, so the client must surface it via the returned
     * {@code ResponseEntity}'s status, never as a thrown exception. That is the reason {@code cancel} returns
     * {@code ResponseEntity<Void>}.
     */
    @Test
    void cancel_404IsReturnedAsStatusNotException() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .post("/v2/discoveryProvider/discoveries/cancel")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(404)
                                .withHeader("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                                .withBody(notTrackedProblemJson("OPERATION_NOT_TRACKED",
                                        "https://docs.otilm.com/problems/connector/discovery/OPERATION_NOT_TRACKED"))));

        DiscoveryRunRequestDto request = new DiscoveryRunRequestDto();
        request.setRunId(RUN_ID);

        ResponseEntity<Void> response = client.cancel(connector, request);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * A conformant not-tracked 404 needs no operator attention, so it must not produce the diagnostic WARN reserved for
     * the ambiguous bare-404 fallback — which would otherwise fire on every routine cancel of an already-finished run
     * and train operators to ignore it.
     */
    @Test
    void cancel_conformantNotTracked404DoesNotWarn() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .post("/v2/discoveryProvider/discoveries/cancel")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(404)
                                .withHeader("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                                .withBody(notTrackedProblemJson("OPERATION_NOT_TRACKED",
                                        "https://docs.otilm.com/problems/connector/discovery/OPERATION_NOT_TRACKED"))));

        DiscoveryRunRequestDto request = new DiscoveryRunRequestDto();
        request.setRunId(RUN_ID);

        ListAppender<ILoggingEvent> recorder = attachClientLogRecorder();
        ResponseEntity<Void> response;
        try {
            response = client.cancel(connector, request);
        } finally {
            detachClientLogRecorder(recorder);
        }

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions
                .assertTrue(warnings(recorder).isEmpty(),
                        () -> "a conformant not-tracked 404 must not warn, got " + recorder.list);
    }

    /**
     * The not-tracked rule is the shared {@link com.otilm.api.model.common.error.ConnectorOperationErrorCodes}, not a
     * local one-code comparison, so it recognises {@code REGISTRATION_NOT_FOUND} as well as
     * {@code OPERATION_NOT_TRACKED}. Pinning the second code keeps every consumer classifying a connector's answer
     * identically.
     */
    @Test
    void cancel_registrationNotFoundIsSwallowedViaSharedRule() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .post("/v2/discoveryProvider/discoveries/cancel")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(404)
                                .withHeader("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                                .withBody(notTrackedProblemJson("REGISTRATION_NOT_FOUND",
                                        "https://docs.otilm.com/problems/connector/authority/REGISTRATION_NOT_FOUND"))));

        DiscoveryRunRequestDto request = new DiscoveryRunRequestDto();
        request.setRunId(RUN_ID);

        ResponseEntity<Void> response = client.cancel(connector, request);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * A 422 refusal (run past the point of no return) is a real failure and must still throw, so the 404-swallowing
     * branch intercepts only a not-tracked answer, not every {@link ConnectorProblemException}.
     */
    @Test
    void cancel_422StillThrows() {
        String problemJson = """
                {
                  "type": "https://docs.otilm.com/problems/connector/OPERATION_PAST_POINT_OF_NO_RETURN",
                  "title": "Cancel refused — operation past point of no return",
                  "status": 422,
                  "detail": "runId 33333333-3333-3333-3333-333333333333 already committed upstream",
                  "errorCode": "OPERATION_PAST_POINT_OF_NO_RETURN",
                  "timestamp": "2026-08-04T10:00:00Z",
                  "correlationId": "test-corr-cancel-422",
                  "retryable": false
                }
                """;
        mockServer
                .stubFor(WireMock
                        .post("/v2/discoveryProvider/discoveries/cancel")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(422)
                                .withHeader("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                                .withBody(problemJson)));

        DiscoveryRunRequestDto request = new DiscoveryRunRequestDto();
        request.setRunId(RUN_ID);

        ConnectorProblemException ex = Assertions
                .assertThrows(ConnectorProblemException.class, () -> client.cancel(connector, request));

        Assertions.assertEquals(ErrorCode.OPERATION_PAST_POINT_OF_NO_RETURN, ex.getProblemDetail().getErrorCode());
        Assertions.assertEquals(connector, ex.getConnector());
    }

    /**
     * A terse-but-conformant connector can answer 404 without an {@code application/problem+json} body.
     * {@code BaseApiClient}'s legacy fallback maps that shape to
     * {@link com.otilm.api.exception.ConnectorEntityNotFoundException} rather than {@link ConnectorProblemException},
     * and cancel must accept it too, or a legitimately-terminal run would be reported as a failure.
     */
    @Test
    void cancel_legacyNotFoundIsReturnedAsStatusNotException() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .post("/v2/discoveryProvider/discoveries/cancel")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(404)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(springDefaultErrorPage())));

        DiscoveryRunRequestDto request = new DiscoveryRunRequestDto();
        request.setRunId(RUN_ID);

        ResponseEntity<Void> response = client.cancel(connector, request);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * The terse 404 can be terser still: no body at all, and no {@code Content-Type}. A Go connector's
     * {@code w.WriteHeader(404)} sends exactly that, and cancel is the likeliest place to meet it since the route is
     * declared bodiless even on success.
     *
     * <p>
     * {@code BaseApiClient}'s legacy 404 branch reads the body for the exception message with
     * {@code bodyToMono(String.class).flatMap(...)}, which never runs for a zero-length body. Without a
     * {@code defaultIfEmpty} the response filter completes empty, no connector exception is raised, and an unmapped
     * {@link IllegalStateException} escapes instead — so {@code cancel}'s {@code onErrorResume} never sees a
     * {@code ConnectorEntityNotFoundException} and a legitimately-terminal run reaches Core as a hard failure of an
     * uncatchable type.
     */
    @Test
    void cancel_bodiless404IsReturnedAsStatusNotIllegalState() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .post("/v2/discoveryProvider/discoveries/cancel")
                        .willReturn(WireMock.aResponse().withStatus(404)));

        DiscoveryRunRequestDto request = new DiscoveryRunRequestDto();
        request.setRunId(RUN_ID);

        ResponseEntity<Void> response = client.cancel(connector, request);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    /**
     * The lenient bare-404 fallback must stay diagnosable: the shared connector contract also documents 404 as
     * "endpoint not found or not implemented", and the body stubbed here is Spring's unmapped-route error page. A
     * connector that never implemented {@code /discoveries/cancel}, or one reached through a stale base URL, would
     * otherwise be reported as an already-terminal cancellation — a silently failed abort. So the WARN must name both
     * the connector and the path to go check.
     */
    @Test
    void cancel_bare404IsSwallowedButWarnsNamingConnectorAndPath() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .post("/v2/discoveryProvider/discoveries/cancel")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(404)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(springDefaultErrorPage())));

        DiscoveryRunRequestDto request = new DiscoveryRunRequestDto();
        request.setRunId(RUN_ID);

        ListAppender<ILoggingEvent> recorder = attachClientLogRecorder();
        ResponseEntity<Void> response;
        try {
            response = client.cancel(connector, request);
        } finally {
            detachClientLogRecorder(recorder);
        }

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        List<ILoggingEvent> warnings = warnings(recorder);
        Assertions
                .assertEquals(1, warnings.size(),
                        () -> "expected exactly one WARN for the swallowed bare 404, got " + recorder.list);
        String message = warnings.get(0).getFormattedMessage();
        Assertions.assertTrue(message.contains(CONNECTOR_NAME), () -> "the WARN must name the connector: " + message);
        Assertions.assertTrue(message.contains(CANCEL_PATH), () -> "the WARN must name the path: " + message);
    }

    /**
     * Every other lifecycle call keeps the standard mapping — a 404 surfaces as {@link ConnectorProblemException} with
     * {@code errorCode=OPERATION_NOT_TRACKED} — unlike cancel.
     */
    @Test
    void status_404MapsToOperationNotTracked() {
        String problemJson = """
                {
                  "type": "https://docs.otilm.com/problems/connector/discovery/OPERATION_NOT_TRACKED",
                  "title": "Async operation no longer tracked by connector",
                  "status": 404,
                  "detail": "runId 33333333-3333-3333-3333-333333333333 not tracked",
                  "errorCode": "OPERATION_NOT_TRACKED",
                  "timestamp": "2026-08-04T10:00:00Z",
                  "correlationId": "test-corr-status-404",
                  "retryable": false
                }
                """;
        mockServer
                .stubFor(WireMock
                        .post("/v2/discoveryProvider/discoveries/status")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(404)
                                .withHeader("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                                .withBody(problemJson)));

        DiscoveryRunRequestDto request = new DiscoveryRunRequestDto();
        request.setRunId(RUN_ID);

        ConnectorProblemException ex = Assertions
                .assertThrows(ConnectorProblemException.class, () -> client.status(connector, request));

        Assertions.assertEquals(ErrorCode.OPERATION_NOT_TRACKED, ex.getProblemDetail().getErrorCode());
        Assertions.assertEquals(connector, ex.getConnector());
    }

    @Test
    void initiate_422MapsToValidationFailed() {
        String problemJson = """
                {
                  "type": "https://docs.otilm.com/problems/common/VALIDATION_FAILED",
                  "title": "Validation failed",
                  "status": 422,
                  "detail": "resource type 'oids' is not supported",
                  "errorCode": "VALIDATION_FAILED",
                  "timestamp": "2026-08-04T10:00:00Z",
                  "correlationId": "test-corr-initiate-422",
                  "retryable": false
                }
                """;
        mockServer
                .stubFor(WireMock
                        .post("/v2/discoveryProvider/discoveries/initiate")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(422)
                                .withHeader("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                                .withBody(problemJson)));

        DiscoveryInitiateRequestDto request = new DiscoveryInitiateRequestDto();
        request.setRunId(RUN_ID);
        request.setResources(List.of(Resource.CERTIFICATE));

        ConnectorProblemException ex = Assertions
                .assertThrows(ConnectorProblemException.class, () -> client.initiate(connector, request));

        Assertions.assertEquals(ErrorCode.VALIDATION_FAILED, ex.getProblemDetail().getErrorCode());
        Assertions.assertEquals(connector, ex.getConnector());
    }

    @Test
    void resume_410MapsToCheckpointLost() {
        String problemJson = """
                {
                  "type": "https://docs.otilm.com/problems/connector/discovery/CHECKPOINT_LOST",
                  "title": "Discovery checkpoint lost — run cannot be resumed",
                  "status": 410,
                  "detail": "checkpoint for runId 33333333-3333-3333-3333-333333333333 expired",
                  "errorCode": "CHECKPOINT_LOST",
                  "timestamp": "2026-08-04T10:00:00Z",
                  "correlationId": "test-corr-resume-410",
                  "retryable": false
                }
                """;
        mockServer
                .stubFor(WireMock
                        .post("/v2/discoveryProvider/discoveries/resume")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(410)
                                .withHeader("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                                .withBody(problemJson)));

        DiscoveryRunRequestDto request = new DiscoveryRunRequestDto();
        request.setRunId(RUN_ID);

        ConnectorProblemException ex = Assertions
                .assertThrows(ConnectorProblemException.class, () -> client.resume(connector, request));

        Assertions.assertEquals(ErrorCode.CHECKPOINT_LOST, ex.getProblemDetail().getErrorCode());
        Assertions.assertEquals(connector, ex.getConnector());
    }

    /**
     * A {@code results} page larger than the configured read cap must fail fast rather than buffer an unbounded
     * response into memory.
     *
     * <p>
     * The cap lives on the shared {@code WebClient}, so it cannot be dialed down through the process-wide
     * {@code BaseApiClient.prepareWebClient()} singleton without either being ignored (another test may have claimed
     * the tuning first) or shrinking the cap for every other test in the JVM run. Hence the throwaway small-cap client.
     *
     * <p>
     * The stubbed body is syntactically valid JSON — a real {@code DiscoveryResultsResponseDto} shape plus one padding
     * field — so the failure comes from the size gate tripping before parsing, not from malformed input failing around
     * the same size.
     *
     * <p>
     * The codec enforces the cap while decoding the 2xx body, inside {@code retrieve()}, which wraps the breach in a
     * {@link WebClientResponseException} carrying the response status and keeping the original
     * {@link DataBufferLimitException} as its cause. Asserted here through the exception class this client's caller
     * actually sees.
     */
    @Test
    void results_oversizedResponse_failsInsteadOfBuffering() {
        int smallCap = 8 * 1024;
        DiscoveryApiClient smallCapClient = smallCapClient(smallCap);

        String oversizedBody = "{\"items\":[],\"highestSequence\":0,\"more\":false,\"padding\":\""
                + "a".repeat(smallCap * 4) + "\"}";
        mockServer
                .stubFor(WireMock
                        .post("/v2/discoveryProvider/discoveries/results")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(oversizedBody)));

        DiscoveryDrainRequestDto request = new DiscoveryDrainRequestDto();
        request.setRunId(RUN_ID);

        ConnectorServerException ex = Assertions
                .assertThrows(ConnectorServerException.class, () -> smallCapClient.results(connector, request));

        // The status is the one the connector really sent (200), not a synthesized 413.
        Assertions.assertEquals(HttpStatus.OK, ex.getHttpStatus());
        Assertions.assertInstanceOf(WebClientResponseException.class, ex.getCause());
        Assertions.assertInstanceOf(DataBufferLimitException.class, ex.getCause().getCause());
    }

    /**
     * The read cap must bound a whole list response, not each of its elements.
     *
     * <p>
     * {@code toEntityList(X.class)} streams the array through Spring's {@code Jackson2Tokenizer}, whose
     * {@code assertInMemorySize} resets its byte counter every time a top-level element completes, so the cap degrades
     * into a per-element cap and the {@code collectList} assembling the result is unbounded. Every element below is a
     * couple of dozen bytes while the array as a whole is several times over the cap — the shape a connector returning
     * a million tiny objects produces, and the shape an element-wise cap waves through. Decoding an array type instead
     * routes through the codec's bounded {@code decodeToMono}, so the whole response is measured.
     */
    @Test
    void listSupportedResources_oversizedArrayOfSmallElements_failsInsteadOfAssembling() {
        int smallCap = 8 * 1024;
        DiscoveryApiClient smallCapClient = smallCapClient(smallCap);

        // ~27 bytes per element, 1000 elements: no element is near the cap, the array is ~3.5x over it.
        String oversizedArray = "[" + String.join(",", Collections.nCopies(1000, "{\"resource\":\"certificates\"}"))
                + "]";
        Assertions
                .assertTrue(oversizedArray.length() > smallCap * 3,
                        "the stubbed array must be comfortably over the cap");
        mockServer
                .stubFor(WireMock
                        .get("/v2/discoveryProvider/resources")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(oversizedArray)));

        ConnectorServerException ex = Assertions
                .assertThrows(ConnectorServerException.class, () -> smallCapClient.listSupportedResources(connector));

        // As above: the connector's own status (200), never a synthesized 413.
        Assertions.assertEquals(HttpStatus.OK, ex.getHttpStatus());
    }

    /**
     * A throwaway {@code WebClient} with its own small read cap, because the shared
     * {@code BaseApiClient.prepareWebClient()} singleton cannot be re-tuned per test — see
     * {@code results_oversizedResponse_failsInsteadOfBuffering}.
     */
    private DiscoveryApiClient smallCapClient(int maxInMemorySize) {
        ExchangeStrategies strategies = ExchangeStrategies.builder().codecs(c -> {
            c.defaultCodecs().maxInMemorySize(maxInMemorySize);
            ApiClientCodecs.configureJsonCodecs(c);
        }).build();
        WebClient smallCapWebClient = WebClient.builder().exchangeStrategies(strategies).build();
        return new DiscoveryApiClient(smallCapWebClient, null);
    }

    /** An RFC 9457 body whose {@code errorCode} the shared not-tracked rule must recognise. */
    private static String notTrackedProblemJson(String errorCode, String type) {
        return """
                {
                  "type": "%s",
                  "title": "Async operation no longer tracked by connector",
                  "status": 404,
                  "detail": "runId 33333333-3333-3333-3333-333333333333 not tracked",
                  "errorCode": "%s",
                  "timestamp": "2026-08-04T10:00:00Z",
                  "correlationId": "test-corr-cancel-404",
                  "retryable": false
                }
                """.formatted(type, errorCode);
    }

    /**
     * Spring's default unmapped-route error page: a 404 with no {@code application/problem+json} body, what a connector
     * that never implemented the endpoint returns.
     */
    private static String springDefaultErrorPage() {
        return "{\"timestamp\":\"2026-08-06T10:00:00Z\",\"status\":404,\"error\":\"Not Found\"," + "\"path\":\""
                + CANCEL_PATH + "\"}";
    }

    /**
     * Attach a recorder to this client's own logger so a test can assert on the events it emits.
     *
     * <p>
     * logback-classic is this module's SLF4J provider (slf4j-simple is on the test classpath too but loses provider
     * selection), so asserting the type makes a provider change fail loudly instead of silently skipping the log
     * assertions. The level is pinned so the assertions do not depend on the ambient root level, and restored to
     * inherited — the module ships no logback configuration, so inherited is the original state.
     */
    private static ListAppender<ILoggingEvent> attachClientLogRecorder() {
        Logger clientLogger = Assertions
                .assertInstanceOf(Logger.class, LoggerFactory.getLogger(DiscoveryApiClient.class),
                        "expected logback-classic to be the active SLF4J provider");
        ListAppender<ILoggingEvent> recorder = new ListAppender<>();
        recorder.setContext(clientLogger.getLoggerContext());
        recorder.start();
        clientLogger.setLevel(Level.WARN);
        clientLogger.addAppender(recorder);
        return recorder;
    }

    private static void detachClientLogRecorder(ListAppender<ILoggingEvent> recorder) {
        Logger clientLogger = (Logger) LoggerFactory.getLogger(DiscoveryApiClient.class);
        clientLogger.detachAppender(recorder);
        clientLogger.setLevel(null);
        recorder.stop();
    }

    private static List<ILoggingEvent> warnings(ListAppender<ILoggingEvent> recorder) {
        return recorder.list.stream().filter(event -> event.getLevel() == Level.WARN).toList();
    }
}
