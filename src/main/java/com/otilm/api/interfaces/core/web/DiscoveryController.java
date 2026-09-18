package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.AttributeException;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.exception.SchedulerException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.certificate.DiscoveryResponseDto;
import com.otilm.api.model.client.certificate.SearchRequestDto;
import com.otilm.api.model.client.discovery.DiscoveryCertificateResponseDto;
import com.otilm.api.model.client.discovery.DiscoveryDetailDto;
import com.otilm.api.model.client.discovery.DiscoveryDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.common.PaginationResponseDto;
import com.otilm.api.model.common.UuidDto;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.discovery.v2.DiscoverySupportedResourceDto;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.discovery.DiscoveryItemDto;
import com.otilm.api.model.core.discovery.DiscoveryMessageDto;
import com.otilm.api.model.core.scheduler.ScheduleDiscoveryDto;
import com.otilm.api.model.core.search.ConfigurableColumnsDocs;
import com.otilm.api.model.core.search.SearchFieldDataByGroupDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.cert.CertificateException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Core-web discovery management: run CRUD plus the discovery v2 additions — a resource-agnostic item listing, a paged
 * run-messages listing, connector-keyed relay endpoints and run-lifecycle operations.
 *
 * <p>
 * <b>Connector-keyed relays.</b> {@link #listDiscoveryResources}, {@link #getDiscoveryAttributes} and
 * {@link #getDiscoveryResourceAttributes} describe a Discovery Provider before any run exists. They live on this
 * controller rather than {@code ConnectorController} because they are specific to one provider interface, while that
 * controller's attribute endpoints are generic across function groups and cannot express a resource dimension.
 *
 * <p>
 * <b>Run-lifecycle legality.</b> Stop, resume and cancel share one legality matrix. Read {@code inProgress} as covering
 * both the scanning and the incremental download phases, which interleave in discovery v2.
 *
 * <pre>
 *            inProgress   stopped   processing   terminal
 *   stop        legal       422        422         422
 *   resume       422       legal       422         422
 *   cancel      legal      legal       422         422
 * </pre>
 *
 * <p>
 * Once a run reaches {@code processing} the Discovery Provider owns nothing and the remaining work is not abortable,
 * which is why all three are refused there as well as in a terminal status. Every refusal is a 422, whether the run's
 * status makes the operation illegal or the run's Provider does not support it at all — the same
 * {@code ValidationException} shape the platform uses for every other illegal state transition.
 */
@RequestMapping("/v1/discoveries")
@Tag(name = "Discovery Management", description = "Discovery Management API")
@ApiResponses(value = {
        @ApiResponse(responseCode = "502", description = "Connector Error",
                content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
        @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})

public interface DiscoveryController extends AuthProtectedController {

    @Operation(summary = "List Discovery",
            description = ConfigurableColumnsDocs.SORT_AND_COLUMNS + ConfigurableColumnsDocs.ATTRIBUTE_PROJECTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of available Discoveries"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(path = "/list", produces = {"application/json"})
    DiscoveryResponseDto listDiscoveries(@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            schema = @Schema(implementation = SearchRequestDto.class),
            examples = {
                    @ExampleObject(name = "With ordering and columns",
                            value = """
                                    {
                                      "pageNumber": 1,
                                      "itemsPerPage": 10,
                                      "filters": [],
                                      "sort": {"fieldSource": "property", "fieldIdentifier": "DISCOVERY_START_TIME", "direction": "desc"},
                                      "columns": [
                                        {"fieldSource": "property", "fieldIdentifier": "DISCOVERY_NAME"},
                                        {"fieldSource": "property", "fieldIdentifier": "DISCOVERY_STATUS"},
                                        {"fieldSource": "property", "fieldIdentifier": "DISCOVERY_KIND"}
                                      ]
                                    }""")})) @Valid @RequestBody SearchRequestDto request);

    @Operation(summary = "Discovery Details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discovery details retrieved"),
            @ApiResponse(responseCode = "404", description = "Discovery not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/{uuid}", produces = {"application/json"})
    DiscoveryDetailDto getDiscovery(@Parameter(description = "Discovery UUID") @PathVariable String uuid)
            throws NotFoundException;

    @Operation(summary = "Discovery Details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discovery details retrieved"),
            @ApiResponse(responseCode = "404", description = "Discovery not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/{uuid}/certificates", produces = {"application/json"})
    DiscoveryCertificateResponseDto getDiscoveryCertificates(
            @Parameter(description = "Discovery UUID") @PathVariable String uuid,
            @RequestParam(required = false) Boolean newlyDiscovered,
            @RequestParam(required = false, defaultValue = "10") int itemsPerPage,
            @RequestParam(required = false, defaultValue = "0") int pageNumber) throws NotFoundException;

    /**
     * Lists everything a run staged, whatever its resource type — the resource-agnostic companion to
     * {@link #getDiscoveryCertificates}, which keeps its certificate-specific columns; both exist on purpose.
     *
     * <p>
     * <b>Response:</b> {@link PaginationResponseDto} of {@link DiscoveryItemDto} — the shared paging envelope, and
     * Core's own view of a staged item rather than the connector's, because processing outcome is Core's to know.
     *
     * <p>
     * <b>Filters:</b> {@code resource} narrows to one resource type by wire code; {@code newlyDiscovered} behaves
     * exactly as on the certificate listing. Both optional; omitted means no filter.
     *
     * <p>
     * <b>Binding:</b> {@code resource} binds by wire code, never by Java enum member name — the implementation
     * registers a wire-code binder for it (Core uses its per-controller {@code @InitBinder} editor pattern).
     */
    @Operation(summary = "List Discovered Items",
            description = "Returns one page of the items this Discovery staged, ordered by their run "
                    + "sequence, optionally narrowed to a single resource type. Certificates are included: a run "
                    + "against a v1 Discovery Provider stages certificates only, so its unfiltered listing contains "
                    + "exactly what the certificate listing contains, with sequence and uniqueRef synthesized by the "
                    + "platform (staging order and certificate fingerprint) because a v1 provider never numbered its "
                    + "items.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discovered items retrieved"),
            @ApiResponse(responseCode = "404", description = "Discovery not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/{uuid}/items", produces = {"application/json"})
    PaginationResponseDto<DiscoveryItemDto> getDiscoveryItems(
            @Parameter(description = "Discovery UUID") @PathVariable String uuid,
            @Parameter(
                    description = "Resource type to list, identified by its wire code (e.g. \"certificates\", \"keys\"); omit to list every resource type this run discovered") @RequestParam(
                            required = false) Resource resource,
            @RequestParam(required = false) Boolean newlyDiscovered,
            @RequestParam(required = false, defaultValue = "10") int itemsPerPage,
            @RequestParam(required = false, defaultValue = "0") int pageNumber) throws NotFoundException;

    /**
     * The run's advisory message log, paged rather than carried on the detail — see
     * {@link com.otilm.api.model.client.discovery.DiscoveryDetailDto}'s {@code runMessageCount} for why it is counted
     * there and read here.
     *
     * <p>
     * The order the operation promises is the order the platform recorded each problem in, deliberately not "by
     * timestamp": every message written in one tick shares a transaction-start time, so a timestamp is not a total
     * order and paging on it would drop or repeat rows. The entry that explains a run is usually the one that started
     * it, which is why the first page is the useful one.
     */
    @Operation(summary = "List Discovery Run Messages",
            description = "Returns one page of the advisory messages a Discovery run collected, oldest first and "
                    + "stable across pages. Repeated problems are aggregated into a single entry carrying an "
                    + "occurrence count rather than repeated per occurrence. Messages are advisory: collecting them "
                    + "does not imply the run failed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discovery run messages retrieved"),
            @ApiResponse(responseCode = "404", description = "Discovery not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/{uuid}/messages", produces = {"application/json"})
    PaginationResponseDto<DiscoveryMessageDto> getDiscoveryRunMessages(
            @Parameter(description = "Discovery UUID") @PathVariable String uuid,
            @RequestParam(required = false, defaultValue = "10") int itemsPerPage,
            @RequestParam(required = false, defaultValue = "0") int pageNumber) throws NotFoundException;

    @Operation(summary = "Create Discovery")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Discovery Created",
                    content = @Content(schema = @Schema(implementation = UuidDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),
            @ApiResponse(responseCode = "404", description = "Connector not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<?> createDiscovery(@Valid @RequestBody DiscoveryDto request) throws AlreadyExistException,
            NotFoundException, CertificateException, InterruptedException, ConnectorException, AttributeException;

    @Operation(summary = "Delete Discovery")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Discovery deleted"),
            @ApiResponse(responseCode = "404", description = "Discovery not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422",
                    description = "Delete refused: a v2 Discovery that has not ended must be cancelled first",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @DeleteMapping(path = "/{uuid}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteDiscovery(@Parameter(description = "Discovery UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Delete Multiple Discoveries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Discoveries deleted"),
            @ApiResponse(responseCode = "404", description = "Discovery not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @DeleteMapping(produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void bulkDeleteDiscovery(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Discovery UUIDs",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {
                    @ExampleObject(
                            value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<String> discoveryUuids)
            throws NotFoundException;

    @Operation(operationId = "getDiscoverySearchableFields", summary = "Get Discovery searchable fields information",
            description = ConfigurableColumnsDocs.CATALOGUE_FLAGS)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discovery searchable field information retrieved")})
    @GetMapping(path = "/search", produces = {"application/json"})
    List<SearchFieldDataByGroupDto> getSearchableFieldInformation();

    @Operation(summary = "Schedule Discovery")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Discovery Scheduled",
                    content = @Content(schema = @Schema(implementation = UuidDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),
            @ApiResponse(responseCode = "404", description = "Connector not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PostMapping(path = "/schedule", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<?> scheduleDiscovery(@RequestBody ScheduleDiscoveryDto scheduleDiscoveryDto)
            throws AlreadyExistException, CertificateException, InterruptedException, ConnectorException,
            SchedulerException, AttributeException, NotFoundException;

    /**
     * Lists the resource types a Discovery Provider advertises.
     *
     * <p>
     * <b>Keyed by connector UUID</b>, not by the run UUID the rest of this controller takes — see the interface Javadoc
     * for why the three connector-keyed relays live here. These rules cover all three relays:
     *
     * <p>
     * <b>Authorization:</b> gate on {@code Resource.CONNECTOR} — object-level, the way
     * {@code listAuthorityInstanceAttributes} is gated — never on {@code DISCOVERY}, which has no object access, so
     * gating there would silently skip per-connector ACLs.
     */
    @Operation(summary = "Get discoverable resources of a Discovery Provider",
            description = "Returns the resource types this Connector's discovery interface supports, "
                    + "relayed live from the Connector. For a Connector implementing only the v1 discovery "
                    + "interface, Core synthesizes the single entry \"certificates\" without calling the "
                    + "Connector — a client renders one shape for both generations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discoverable resources retrieved"),
            @ApiResponse(responseCode = "404", description = "Connector not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/{connectorUuid}/resources", produces = {"application/json"})
    List<DiscoverySupportedResourceDto> listDiscoveryResources(
            @Parameter(description = "Discovery Provider Connector UUID") @PathVariable String connectorUuid)
            throws NotFoundException, ConnectorException;

    /**
     * Relays the run-level attribute schema from a Discovery Provider. Connector-keyed relay — keying and authorization
     * rules on {@link #listDiscoveryResources}.
     */
    @Operation(summary = "Get run-level Discovery Attributes from a Discovery Provider",
            description = "Relays the run-level attribute definitions from the Connector's discovery "
                    + "interface: the schema that configures a discovery run as a whole and applies to "
                    + "every resource type the run targets. Exists only for Connectors implementing "
                    + "discovery v2; for a v1 Connector, use the kind-scoped Connector attribute "
                    + "endpoint, exactly as today.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Run-level Discovery Attributes received"),
            @ApiResponse(responseCode = "404", description = "Connector not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Connector does not implement the v2 discovery interface",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @GetMapping(path = "/{connectorUuid}/attributes", produces = {"application/json"})
    List<BaseAttribute> getDiscoveryAttributes(
            @Parameter(description = "Discovery Provider Connector UUID") @PathVariable String connectorUuid)
            throws ValidationException, NotFoundException, ConnectorException;

    /**
     * Relays the attribute schema refining one resource type from a Discovery Provider. Connector-keyed relay — keying
     * and authorization rules on {@link #listDiscoveryResources}.
     */
    @Operation(summary = "Get per-resource Discovery Attributes from a Discovery Provider",
            description = "Relays the attribute definitions that refine discovery of one resource type "
                    + "from the Connector's discovery interface.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Per-resource Discovery Attributes received"),
            @ApiResponse(responseCode = "404", description = "Connector not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422",
                    description = "Resource is not discoverable by this Connector, or the Connector does "
                            + "not implement the v2 discovery interface",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @GetMapping(path = "/{connectorUuid}/{resource}/attributes", produces = {"application/json"})
    List<BaseAttribute> getDiscoveryResourceAttributes(
            @Parameter(description = "Discovery Provider Connector UUID") @PathVariable String connectorUuid,
            @Parameter(
                    description = "Resource type, identified by its wire code (e.g. \"certificates\", \"keys\")") @PathVariable Resource resource)
            throws ValidationException, NotFoundException, ConnectorException;

    @Operation(summary = "Stop Discovery",
            description = "Asks the Discovery Provider to suspend an in-progress run, keeping everything "
                    + "already staged and the Provider-side checkpoint needed to resume it. Legal only "
                    + "while the run is in progress.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Discovery stopped"),
            @ApiResponse(responseCode = "404", description = "Discovery not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422",
                    description = "Discovery is not in progress, or its Discovery Provider does not support "
                            + "stopping a run",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PatchMapping(path = "/{uuid}/stop", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void stopDiscovery(@Parameter(description = "Discovery UUID") @PathVariable String uuid)
            throws ValidationException, NotFoundException, ConnectorException;

    @Operation(summary = "Resume Discovery",
            description = "Asks the Discovery Provider to continue a stopped run from its checkpoint. "
                    + "Legal only from the stopped status. A Provider that has since lost the "
                    + "checkpoint fails the run instead of restarting it, so a resume is never "
                    + "silently a fresh scan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Discovery resumed"),
            @ApiResponse(responseCode = "404", description = "Discovery not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422",
                    description = "Discovery is not stopped, or its Discovery Provider does not support "
                            + "resuming a run",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PatchMapping(path = "/{uuid}/resume", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void resumeDiscovery(@Parameter(description = "Discovery UUID") @PathVariable String uuid)
            throws ValidationException, NotFoundException, ConnectorException;

    @Operation(summary = "Cancel Discovery",
            description = "Abandons a run for good: the Discovery Provider releases it and the items "
                    + "already staged are never processed. Legal while the run is in progress or "
                    + "stopped. This is irreversible - a cancelled run cannot be resumed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Discovery cancelled"),
            @ApiResponse(responseCode = "404", description = "Discovery not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422",
                    description = "Discovery is neither in progress nor stopped, or its Discovery Provider "
                            + "does not support cancelling a run",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PatchMapping(path = "/{uuid}/cancel", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void cancelDiscovery(@Parameter(description = "Discovery UUID") @PathVariable String uuid)
            throws ValidationException, NotFoundException, ConnectorException;

}
