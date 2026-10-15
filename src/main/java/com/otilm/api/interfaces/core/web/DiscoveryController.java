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
 * Discovery management: runs, their staged items and messages, the run lifecycle, and the connector-keyed relays.
 *
 * <p>
 * The relays ({@link #listDiscoveryResources}, {@link #getDiscoveryAttributes},
 * {@link #getDiscoveryResourceAttributes}) describe a Discovery Provider before any run exists. They live here because
 * {@code ConnectorController}'s attribute endpoints are generic across function groups and cannot express a resource
 * dimension.
 *
 * <p>
 * Lifecycle legality, where {@code inProgress} covers both scanning and downloading, which interleave in v2:
 *
 * <pre>
 *            inProgress   stopped   processing   terminal
 *   stop        legal       422        422         422
 *   resume       422       legal       422         422
 *   cancel      legal      legal       422         422
 * </pre>
 *
 * <p>
 * In {@code processing} the Discovery Provider owns nothing and the remaining work cannot be aborted. Every refusal is
 * a 422 {@code ValidationException}, also when the Provider does not support the operation.
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
            @ApiResponse(responseCode = "422", description = "Unprocessable Content",
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

    @Operation(summary = "List Discovered Certificates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discovered certificates retrieved"),
            @ApiResponse(responseCode = "404", description = "Discovery not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/{uuid}/certificates", produces = {"application/json"})
    DiscoveryCertificateResponseDto getDiscoveryCertificates(
            @Parameter(description = "Discovery UUID") @PathVariable String uuid,
            @RequestParam(required = false) Boolean newlyDiscovered,
            @RequestParam(required = false, defaultValue = "10") int itemsPerPage,
            @RequestParam(required = false, defaultValue = "0") int pageNumber) throws NotFoundException;

    /** {@code resource} binds by code, not enum name; the implementation registers a binder for it. */
    @Operation(summary = "List Discovered Items",
            description = "Returns one page of the items the discovery staged, in run sequence, optionally narrowed to "
                    + "one resource type. For a v1 run it lists the same certificates as the certificate listing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discovered items retrieved"),
            @ApiResponse(responseCode = "404", description = "Discovery not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/{uuid}/items", produces = {"application/json"})
    PaginationResponseDto<DiscoveryItemDto> getDiscoveryItems(
            @Parameter(description = "Discovery UUID") @PathVariable String uuid,
            @Parameter(
                    description = "Resource code to list, e.g. \"certificates\" or \"keys\"; omit for every resource type") @RequestParam(
                            required = false) Resource resource,
            @RequestParam(required = false) Boolean newlyDiscovered,
            @RequestParam(required = false, defaultValue = "10") int itemsPerPage,
            @RequestParam(required = false, defaultValue = "0") int pageNumber) throws NotFoundException;

    /**
     * Paged in recording order, not by timestamp: messages written in one tick share a timestamp, so it is no total
     * order.
     */
    @Operation(summary = "List Discovery Run Messages",
            description = "Returns one page of the discovery's run messages, oldest first and stable across pages. A "
                    + "repeated problem is one "
                    + "entry with an occurrence count, and messages alone do not mean the run failed.")
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
            @ApiResponse(responseCode = "422", description = "Unprocessable Content",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),
            @ApiResponse(responseCode = "404", description = "Connector not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "409", description = "Discovery of that name already exists",
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
                    description = "Delete refused: cancel a v2 Discovery that has not ended, or wait for it to "
                            + "end once it is processing and can no longer be cancelled",
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
            @ApiResponse(responseCode = "422", description = "Unprocessable Content",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),
            @ApiResponse(responseCode = "404", description = "Connector not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "409",
                    description = "A Discovery or a scheduled job of that name already exists, including a one-time "
                            + "job that has already run",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PostMapping(path = "/schedule", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<?> scheduleDiscovery(@RequestBody ScheduleDiscoveryDto scheduleDiscoveryDto)
            throws AlreadyExistException, CertificateException, InterruptedException, ConnectorException,
            SchedulerException, AttributeException, NotFoundException;

    /**
     * Keyed by connector UUID. Authorization for every connector-keyed relay is object-level on
     * {@code Resource.CONNECTOR}, never {@code DISCOVERY}, which has no object access and would skip per-connector
     * ACLs.
     */
    @Operation(summary = "Get discoverable resources of a Discovery Provider",
            description = "Returns the resource types the Connector can discover, asked live. For a v1 Connector, Core "
                    + "answers a single certificates entry without calling it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Discoverable resources retrieved"),
            @ApiResponse(responseCode = "404", description = "Connector not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/{connectorUuid}/resources", produces = {"application/json"})
    List<DiscoverySupportedResourceDto> listDiscoveryResources(
            @Parameter(description = "Discovery Provider Connector UUID") @PathVariable String connectorUuid)
            throws NotFoundException, ConnectorException;

    /** Connector-keyed relay; see {@link #listDiscoveryResources}. */
    @Operation(summary = "Get run-level Discovery Attributes from a Discovery Provider",
            description = "Returns the attributes that configure a run as a whole, from a v2 Discovery Provider. For a "
                    + "v1 Connector, use the kind-scoped Connector attribute endpoint.")
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

    /** Connector-keyed relay; see {@link #listDiscoveryResources}. */
    @Operation(summary = "Get per-resource Discovery Attributes from a Discovery Provider",
            description = "Returns the attributes that refine discovery of one resource type, from a v2 Discovery "
                    + "Provider.")
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
                    description = "Resource code, e.g. \"certificates\" or \"keys\"") @PathVariable Resource resource)
            throws ValidationException, NotFoundException, ConnectorException;

    @Operation(summary = "Stop Discovery",
            description = "Suspends an in-progress discovery so it can be resumed, keeping what it has staged.")
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
            description = "Continues a stopped discovery where it stopped. If the Discovery Provider has lost its "
                    + "checkpoint, the discovery fails rather than starting over.")
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
            description = "Ends an in-progress or stopped discovery for good; the items it staged are not "
                    + "processed.")
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
