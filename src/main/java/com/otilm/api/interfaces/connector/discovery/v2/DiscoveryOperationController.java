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
                + "Every call replays the run's identity, resources, checkpoint and attributes, so store none of "
                + "them: rebuild the run from the request, also after a restart. In buffered delivery, hold only the "
                + "run's undrained items, as the drain operation requires; in checkpointed delivery, hold nothing "
                + "between calls. Answer 404 only when you cannot serve the run from the request: a runId you cannot "
                + "rebuild, or, in checkpointed delivery, a replayed checkpoint that disagrees with afterSequence. It "
                + "ends the run at once, and a stopped run may wait days for its resume. In checkpointed delivery, a "
                + "checkpoint you cannot read is an invalid request rather than a run you cannot rebuild: answer 422 "
                + "with errorCode VALIDATION_FAILED.\n\n"
                + "In buffered delivery, if you can rebuild the run but no longer hold the items a drain asks for, "
                + "answer 404 rather than a later page: Core takes the highest sequence returned as its cursor, so a "
                + "page served across a gap loses everything in it.")
public interface DiscoveryOperationController extends AuthProtectedConnectorController {

    /** Shared by every body-taking operation: the scoped base makes runId and resources mandatory on all of them. */
    String VALIDATION_FAILED = "The request body is invalid — a missing runId, an empty or unsupported "
            + "resources set, or an invalid attribute (errorCode VALIDATION_FAILED)";

    @Operation(summary = "Initiate a discovery run",
            description = "Starts a run for the given resource types and attributes. runId comes from Core and is "
                    + "never reused. The connector MUST validate the whole request, including that every resource type "
                    + "is supported, and reject it with 422 rather than narrow the resource set. A repeat with the "
                    + "same runId and payload MUST be answered idempotently: another 202, without starting a second "
                    + "run.\n\n"
                    + "Core lists the delivery modes it supports in deliveryModes, in its order of preference, and the "
                    + "connector answers the one this run uses in deliveryMode. A response without deliveryMode means "
                    + "buffered delivery. A connector that supports none of the offered delivery modes MUST answer 422 "
                    + "with errorCode VALIDATION_FAILED and a detail that names the reason, such as the minimum "
                    + "platform version it needs.")
    @ApiResponses({
            @ApiResponse(responseCode = "202",
                    description = "Run accepted, with its checkpoint, its delivery mode and, optionally, whether it can "
                            + "be stopped"),
            @ApiResponse(responseCode = "422",
                    description = "One or more requested resources or attributes are unsupported or invalid, or none of "
                            + "the offered delivery modes is supported (errorCode VALIDATION_FAILED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/initiate", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    DiscoveryInitiateResponseDto initiate(@Valid @RequestBody DiscoveryInitiateRequestDto request);

    @Operation(summary = "Get discovery run status",
            description = "Returns the run's state and progress, and its metadata when the connector has some. In "
                    + "checkpointed delivery, report them from the replayed checkpoint.")
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
                    + "the run ended; after it, the connector MAY discard everything. In checkpointed delivery the "
                    + "connector meets this by answering from the replayed checkpoint and keeps nothing.\n\n"
                    + "In buffered delivery, size a bounded result buffer for Core's polling interval: after an empty "
                    + "drain, the next one waits out the interval, tens of seconds by default and "
                    + "operator-configurable.\n\n"
                    + "In checkpointed delivery, the connector keeps nothing between calls, and each call does a bounded "
                    + "amount of work from the replayed checkpoint:\n"
                    + "- Number the page's items densely from afterSequence + 1.\n"
                    + "- Return the checkpoint after its items in checkpoint, on every page, an empty one included. Core "
                    + "stores the items, its cursor and that checkpoint together and replays the checkpoint on the next "
                    + "call.\n"
                    + "- more: true means call again now; more: false means nothing more is ready now. Whether the run "
                    + "has finished comes from status. A page with no items but a moved checkpoint is progress, not "
                    + "repetition.\n"
                    + "- A connector waiting on its source answers a pending page: no items, the checkpoint unchanged and "
                    + "more: false. Core calls again after its idle interval and does not count it as a failure.\n"
                    + "- Core never asks for fewer than 100 items or less than 1 MiB per page, except in the "
                    + "acknowledgement: a drain with afterSequence equal to highestSequence on a finished run, where "
                    + "nothing remains to send.\n"
                    + "- Never split one unit of work, such as one scanned target, across pages. If a single unit's "
                    + "items exceed maxItems or maxBytes, answer 422 with errorCode VALIDATION_FAILED naming the limit.\n"
                    + "- Any 422 on a results call ends a checkpointed run at once, so answer it only for a condition a "
                    + "retry cannot clear, such as an oversized unit of work or a checkpoint the connector cannot read.\n"
                    + "- A connector too busy to take the call answers 503 with errorCode SERVICE_UNAVAILABLE. Core calls "
                    + "again shortly and does not count it as a failure until the run has received nothing but 503s "
                    + "for a bounded time, 30 minutes by default.\n"
                    + "- A drain whose deliveryModes does not offer checkpointed comes from a Core that does not drive "
                    + "checkpointed runs, such as an older one that would not store the page's checkpoint. Answer "
                    + "it 503 with errorCode SERVICE_UNAVAILABLE, so a Core that offers checkpointed can take the "
                    + "retry.\n"
                    + "- Bound the work so the call answers within 20 seconds; Core's default call timeout is 30 "
                    + "seconds.\n"
                    + "- When afterSequence and the replayed checkpoint disagree, answer 404 with errorCode "
                    + "OPERATION_NOT_TRACKED: the checkpoint is stale or items were lost, and serving a page anyway "
                    + "would hide that.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Items retrieved; more: true means call again now, more: false means nothing more is "
                            + "ready now"),
            @ApiResponse(responseCode = "404",
                    description = "Run not tracked by the connector, or, in checkpointed delivery, afterSequence and "
                            + "the replayed checkpoint disagree (errorCode OPERATION_NOT_TRACKED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = VALIDATION_FAILED + "; in checkpointed delivery any 422 ends the run, for example "
                            + "for a single unit of work whose items exceed maxItems or maxBytes, or a checkpoint the "
                            + "connector cannot read",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "503",
                    description = "The connector is too busy to take the call now, or, in checkpointed delivery, the "
                            + "drain does not offer checkpointed (errorCode SERVICE_UNAVAILABLE). In checkpointed "
                            + "delivery Core calls again shortly and does not count it as a failure, for a bounded "
                            + "time; in buffered delivery it counts as an unanswered drain",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/results", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    DiscoveryResultsResponseDto results(@Valid @RequestBody DiscoveryDrainRequestDto request);

    @Operation(summary = "Stop a discovery run", description = "Halts scanning of a running run.\n\n"
            + "In buffered delivery, return a best-effort checkpoint, which MUST preserve the item sequence so a "
            + "resume continues numbering where the run stopped. Items not yet drained stay drainable while the "
            + "run is stopped.\n\n"
            + "In checkpointed delivery, answer without a checkpoint: Core keeps the checkpoint it holds. A stop "
            + "past the point of no return is still refused with 422. Core discards a results page "
            + "that arrives after a stop, so no special handling is needed, and it sends no status or results call "
            + "while the run is stopped; resume restarts them.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Run stopped; in buffered delivery, checkpoint carries the handle to resume from"),
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
                    + "run stopped; a resumed run never restarts numbering.\n\n"
                    + "As on initiate, Core lists the delivery modes it supports and the connector answers the one the "
                    + "run uses from now on, which may differ from before, for example after a connector upgrade. A "
                    + "connector that supports none of the offered delivery modes MUST answer 422 with errorCode "
                    + "VALIDATION_FAILED.\n\n"
                    + "In checkpointed delivery, answer without a checkpoint: Core keeps the checkpoint it holds. That is "
                    + "the one from the last page or, before the first page, from initiate; or the stop's checkpoint "
                    + "when the run was buffered before it stopped. A stop's checkpoint can count items Core never "
                    + "received; the first drain then finds afterSequence and the checkpoint disagreeing.")
    @ApiResponses({
            @ApiResponse(responseCode = "202",
                    description = "Resume accepted, with the delivery mode the run uses from now on, optionally whether "
                            + "the run can be stopped again and, in buffered delivery, the possibly updated checkpoint"),
            @ApiResponse(responseCode = "404",
                    description = "Run not tracked by the connector (errorCode OPERATION_NOT_TRACKED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "410",
                    description = "The checkpoint needed to resume is no longer available (errorCode CHECKPOINT_LOST); "
                            + "404 means the run is not known at all",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = VALIDATION_FAILED + "; or none of the offered delivery modes is supported",
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
