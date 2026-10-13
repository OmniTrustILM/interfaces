package com.otilm.api.interfaces.connector.discovery.v2;

import com.otilm.api.interfaces.connector.common.v2.AuthProtectedConnectorController;
import com.otilm.api.model.common.error.ProblemDetailExtended;
import com.otilm.api.model.connector.discovery.v2.DiscoveryDrainRequestDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryInitiateRequestDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryInitiateResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryResultsResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryRunRequestDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryStatusResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryStopResponseDto;
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

@RequestMapping("/v2/discoveryProvider/discoveries")
@Tag(name = "Discovery Operations v2",
        description = "Run lifecycle: initiate a run, poll its status and results, and stop, resume or cancel it.\n\n"
                + "Every call replays the run's identity, resources, checkpoint and attributes, so keep no state "
                + "between calls and rebuild the run from the request, also after a restart. Answer 404 only for a "
                + "runId you cannot rebuild: it ends the run at once, and a stopped run may wait days for its "
                + "resume.\n\n"
                + "If you can rebuild the run but no longer hold the items a drain asks for, answer 404 rather than a "
                + "later page: Core takes the highest sequence returned as its cursor, so a page served across a gap "
                + "loses everything in it.")
public interface DiscoveryOperationController extends AuthProtectedConnectorController {

    /** Shared by every body-taking operation: the scoped base makes runId and resources mandatory on all of them. */
    String VALIDATION_FAILED = "The request body is invalid — a missing runId, an empty or unsupported "
            + "resources set, or an invalid attribute (errorCode VALIDATION_FAILED)";

    @Operation(summary = "Initiate a discovery run",
            description = "Starts a run for the given resource types and attributes. runId comes from Core and is "
                    + "never reused. The connector MUST validate the whole request, including that every resource type "
                    + "is supported, and reject it with 422 rather than narrow the resource set. A repeat with the "
                    + "same runId and payload MUST be answered with another 202.")
    @ApiResponses({
            @ApiResponse(responseCode = "202",
                    description = "Run accepted, with its checkpoint and, optionally, whether it can be stopped"),
            @ApiResponse(responseCode = "422",
                    description = "One or more requested resources or attributes are unsupported or invalid (errorCode VALIDATION_FAILED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/initiate", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    DiscoveryInitiateResponseDto initiate(@Valid @RequestBody DiscoveryInitiateRequestDto request);

    @Operation(summary = "Get discovery run status",
            description = "Returns the run's state and progress, and its metadata when the connector has some.")
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
            description = "Returns one page of items after afterSequence, within maxItems and maxBytes. Draining is "
                    + "allowed at any point of a live run.\n\n"
                    + "A drain acknowledges every item at or below its afterSequence, which the connector MAY then "
                    + "discard. Once the run is terminal, a drain with afterSequence equal to highestSequence is the "
                    + "full ack. Until it arrives, the connector MUST keep the run's state for at least 24 hours after "
                    + "the run ended; after it, the connector MAY discard everything.\n\n"
                    + "Size a bounded result buffer for Core's polling interval: after an empty drain, the next one "
                    + "waits out the interval, tens of seconds by default.")
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

    @Operation(summary = "Stop a discovery run",
            description = "Halts scanning of a running run and returns a checkpoint, which MUST preserve the item "
                    + "sequence so a resume continues numbering where the run stopped. Items not yet drained stay "
                    + "drainable while the run is stopped.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Run stopped; checkpoint carries the handle to resume from"),
            @ApiResponse(responseCode = "404",
                    description = "Run not tracked by the connector (errorCode OPERATION_NOT_TRACKED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = "Stop refused: past the point of no return, or the run cannot be checkpointed "
                            + "(errorCode OPERATION_PAST_POINT_OF_NO_RETURN); or the request body is invalid "
                            + "(errorCode VALIDATION_FAILED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/stop", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    DiscoveryStopResponseDto stop(@Valid @RequestBody DiscoveryRunRequestDto request);

    @Operation(summary = "Resume a stopped discovery run",
            description = "Continues a stopped run from the replayed checkpoint. Item sequences continue where the "
                    + "run stopped; a resumed run never restarts numbering.")
    @ApiResponses({
            @ApiResponse(responseCode = "202",
                    description = "Resume accepted, with the possibly updated checkpoint and, optionally, whether the "
                            + "run can be stopped again"),
            @ApiResponse(responseCode = "404",
                    description = "Run not tracked by the connector (errorCode OPERATION_NOT_TRACKED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "410",
                    description = "The checkpoint needed to resume is no longer available (errorCode CHECKPOINT_LOST); "
                            + "404 means the run is not known at all",
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
            description = "Ends the run for good; the connector discards its state once it has answered.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Run cancelled"),
            @ApiResponse(responseCode = "404",
                    description = "Run not tracked by the connector (errorCode OPERATION_NOT_TRACKED); Core treats it "
                            + "as already cancelled",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = "Cancel refused: the run is past the point of no return (errorCode OPERATION_PAST_POINT_OF_NO_RETURN), or the request body is invalid (errorCode VALIDATION_FAILED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void cancel(@Valid @RequestBody DiscoveryRunRequestDto request);
}
