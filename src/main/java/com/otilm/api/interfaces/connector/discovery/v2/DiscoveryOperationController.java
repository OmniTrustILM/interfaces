package com.otilm.api.interfaces.connector.discovery.v2;

import com.otilm.api.interfaces.connector.common.v2.AuthProtectedConnectorController;
import com.otilm.api.model.common.error.ProblemDetailExtended;
import com.otilm.api.model.connector.discovery.v2.DiscoveryDrainRequestDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryEvent;
import com.otilm.api.model.connector.discovery.v2.DiscoveryInitiateRequestDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryInitiateResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryResultsResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryRunRequestDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryStatusResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryStopResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryStreamRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Flux;

@RequestMapping("/v2/discoveryProvider/discoveries")
@Tag(name = "Discovery Operations v2",
        description = "Stateless discovery v2 run lifecycle: initiate a run, poll or stream its results, and "
                + "control it (stop/resume/cancel).\n\n"
                + "Every call replays the run's identity, resources, handle and attributes, so you need keep "
                + "no state between calls: rebuild the run from the request, including after a restart. "
                + "Answer 404 only for a runId you cannot rebuild — it ends the run at once, and a stopped "
                + "run may be waiting days for its resume.\n\n"
                + "If you can rebuild the run but no longer hold the items a drain asks for, answer 404 "
                + "rather than a later page: Core takes the highest sequence you return as its new cursor, so "
                + "a page served across a gap loses everything in it.")
public interface DiscoveryOperationController extends AuthProtectedConnectorController {

    /** Shared by every body-taking operation: the scoped base makes runId and resources mandatory on all of them. */
    String VALIDATION_FAILED = "The request body is invalid — a missing runId, an empty or unsupported "
            + "resources set, or an invalid attribute (errorCode VALIDATION_FAILED)";

    @Operation(summary = "Initiate a discovery run",
            description = "Starts a new discovery run for the given resource types and attributes. runId is "
                    + "minted by Core and never reused. The connector MUST validate the full payload — "
                    + "including that every requested resource type is supported — and reject the whole "
                    + "request with 422 rather than silently narrowing the resource set. Submitting the same "
                    + "runId again with an identical payload MUST be answered idempotently with another 202.")
    @ApiResponses({
            @ApiResponse(responseCode = "202",
                    description = "Run accepted; meta carries the connector's opaque run handle, replayed on every "
                            + "subsequent lifecycle call, and stoppable, when present, declares whether this run "
                            + "can be stopped and resumed"),
            @ApiResponse(responseCode = "422",
                    description = "One or more requested resources or attributes are unsupported or invalid (errorCode VALIDATION_FAILED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/initiate", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    DiscoveryInitiateResponseDto initiate(@Valid @RequestBody DiscoveryInitiateRequestDto request);

    @Operation(summary = "Get discovery run status", description = "Returns the run's current state and progress.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Run status retrieved"),
            @ApiResponse(responseCode = "404",
                    description = "Run not tracked by the connector (errorCode OPERATION_NOT_TRACKED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422", description = VALIDATION_FAILED,
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/status", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    DiscoveryStatusResponseDto status(@Valid @RequestBody DiscoveryRunRequestDto request);

    @Operation(summary = "Drain discovered items",
            description = "Returns one page of items discovered after afterSequence, bounded by maxItems "
                    + "and maxBytes. This call doubles as the acknowledgment mechanism: submitting a given "
                    + "afterSequence is the implicit ack for every item at or below it, and the connector MAY "
                    + "discard those items once it has answered. Draining is legal at any point during a "
                    + "live run. A drain with afterSequence equal to highestSequence, made after the run has "
                    + "reached a terminal state, is the full ack. Until the full ack arrives, the connector "
                    + "MUST retain terminal-run state for at least 24 hours after the run reached a terminal "
                    + "state. Once the full ack arrives, the connector MAY discard all state it holds for the "
                    + "run.\n\n"
                    + "A connector that bounds its result buffer should size it against the polling interval "
                    + "rather than assume prompt collection. While items are flowing Core drains again "
                    + "immediately, but a drain that returns nothing leaves the next one already scheduled, so "
                    + "production resuming just after that drain waits out the interval before anything is "
                    + "collected. It is tens of seconds by default and is Core-side configuration an operator "
                    + "may widen, so treat it as a bound to cover rather than a fixed number.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Items retrieved; more indicates whether additional items remain beyond this page"),
            @ApiResponse(responseCode = "404",
                    description = "Run not tracked by the connector (errorCode OPERATION_NOT_TRACKED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422", description = VALIDATION_FAILED,
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/results", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    DiscoveryResultsResponseDto results(@Valid @RequestBody DiscoveryDrainRequestDto request);

    @Operation(summary = "Stream discovery events",
            description = "Opens a chunked application/x-ndjson response, one JSON event object per line, "
                    + "replaying items with sequence greater than afterSequence and then continuing to emit "
                    + "live events (progress, resultBatch, stateChanged, heartbeat, error) until the run "
                    + "reaches a terminal state or the caller disconnects. Only one stream may be active per "
                    + "run at a time: a new call to this endpoint for the same runId supersedes any prior "
                    + "one, and the connector MUST close the earlier response as it begins serving the new "
                    + "one. This endpoint is reached by direct HTTP only and never traverses the platform "
                    + "proxy; a connector reachable only through the proxy MUST NOT advertise the "
                    + "discoveryStreaming feature flag.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "NDJSON event stream opened; the body is one flat JSON "
                            + "DiscoveryEvent object per line, never a JSON array of events",
                    content = @Content(mediaType = MediaType.APPLICATION_NDJSON_VALUE,
                            schema = @Schema(implementation = DiscoveryEvent.class))),
            @ApiResponse(responseCode = "404",
                    description = "Run not tracked by the connector (errorCode OPERATION_NOT_TRACKED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422", description = VALIDATION_FAILED,
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/stream", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_NDJSON_VALUE)
    Flux<DiscoveryEvent> stream(@Valid @RequestBody DiscoveryStreamRequestDto request);

    @Operation(summary = "Stop a discovery run",
            description = "Halts scanning for an in-progress run. The connector persists a best-effort "
                    + "checkpoint into the returned meta; this checkpoint MUST preserve the item sequence "
                    + "counter, so a subsequent resume continues assigning sequences from where the run left "
                    + "off. Items already discovered but not yet drained remain drainable via POST /results "
                    + "while the run is stopped.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Run stopped; meta carries the checkpoint to resume from"),
            @ApiResponse(responseCode = "404",
                    description = "Run not tracked by the connector (errorCode OPERATION_NOT_TRACKED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = "Run cannot be stopped: past the point of no return, or the connector cannot checkpoint this run (errorCode OPERATION_PAST_POINT_OF_NO_RETURN). Core's own gate rejects stops for runs whose effective stoppability is false before they reach the connector, so this refusal is the connector's runtime authority",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/stop", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    DiscoveryStopResponseDto stop(@Valid @RequestBody DiscoveryRunRequestDto request);

    @Operation(summary = "Resume a stopped discovery run",
            description = "Continues a stopped run from the checkpoint carried in the replayed meta. Item "
                    + "sequences assigned after resuming continue the run's existing sequence space; the "
                    + "connector never restarts numbering for a resumed run.")
    @ApiResponses({
            @ApiResponse(responseCode = "202",
                    description = "Resume accepted; meta carries the connector's (possibly updated) opaque run handle, "
                            + "and stoppable, when present, re-declares whether the resumed run can be stopped again"),
            @ApiResponse(responseCode = "404",
                    description = "Run not tracked by the connector (errorCode OPERATION_NOT_TRACKED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "410",
                    description = "The checkpoint needed to resume this run is no longer available (errorCode CHECKPOINT_LOST); distinct from 404, which means the run was never known",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422", description = VALIDATION_FAILED,
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/resume", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    DiscoveryInitiateResponseDto resume(@Valid @RequestBody DiscoveryRunRequestDto request);

    @Operation(summary = "Cancel a discovery run",
            description = "Terminally aborts the run; the connector discards its job state for this runId "
                    + "once it has answered.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Run cancelled"),
            @ApiResponse(responseCode = "404",
                    description = "Run not tracked by the connector (errorCode OPERATION_NOT_TRACKED); treat as an already-terminal cancellation rather than an error",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = "Cancel refused: the run is past the point of no return (errorCode OPERATION_PAST_POINT_OF_NO_RETURN)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void cancel(@Valid @RequestBody DiscoveryRunRequestDto request);
}
