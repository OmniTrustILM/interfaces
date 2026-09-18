package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.CbomRepositoryException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.certificate.SearchRequestDto;
import com.otilm.api.model.common.BulkActionMessageDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.common.PaginationResponseDto;
import com.otilm.api.model.core.cbom.CbomDetailDto;
import com.otilm.api.model.core.cbom.CbomDto;
import com.otilm.api.model.core.cbom.CbomSyncSkipDto;
import com.otilm.api.model.core.cbom.CbomUploadRequestDto;
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
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/v1/cboms")
@Tag(name = "CBOM management", description = "CBOM management API")
public interface CbomController extends AuthProtectedController {

    @Operation(summary = "List CBOMs",
            description = ConfigurableColumnsDocs.SORT_AND_COLUMNS + ConfigurableColumnsDocs.ATTRIBUTE_PROJECTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of available CBOMs"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    PaginationResponseDto<CbomDto> listCboms(@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            schema = @Schema(implementation = SearchRequestDto.class),
            examples = {@ExampleObject(name = "With ordering and columns", value = """
                    {
                      "pageNumber": 1,
                      "itemsPerPage": 10,
                      "filters": [],
                      "sort": {"fieldSource": "property", "fieldIdentifier": "CBOM_TIMESTAMP", "direction": "desc"},
                      "columns": [
                        {"fieldSource": "property", "fieldIdentifier": "CBOM_SERIAL_NUMBER"},
                        {"fieldSource": "property", "fieldIdentifier": "CBOM_VERSION"},
                        {"fieldSource": "property", "fieldIdentifier": "CBOM_TIMESTAMP"}
                      ]
                    }""")})) @Valid @RequestBody SearchRequestDto request);

    @Operation(summary = "CBOM detail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CBOM details retrieved"),
            @ApiResponse(responseCode = "404", description = "CBOM not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    CbomDetailDto getCbomDetail(@Parameter(description = "CBOM entry UUID") @PathVariable UUID uuid)
            throws NotFoundException, CbomRepositoryException;

    @Operation(summary = "List CBOM versions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of CBOM versions retrieved"),
            @ApiResponse(responseCode = "404", description = "CBOM not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/{uuid}/versions", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<CbomDto> listCbomVersions(@Parameter(description = "CBOM entry UUID") @PathVariable UUID uuid)
            throws NotFoundException;

    @Operation(summary = "Upload CBOM")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "CBOM uploaded"),
            @ApiResponse(responseCode = "400", description = "Invalid CBOM content",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PostMapping(path = "/upload", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    CbomDto uploadCbom(@RequestBody CbomUploadRequestDto request)
            throws ValidationException, AlreadyExistException, CbomRepositoryException;

    @Operation(summary = "Delete CBOM entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "CBOM entry deleted"),
            @ApiResponse(responseCode = "404", description = "CBOM entry not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    void deleteCbom(@Parameter(description = "CBOM entry UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(summary = "Delete multiple CBOM entries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CBOM entries deleted"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @DeleteMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BulkActionMessageDto> bulkDeleteCbom(@RequestBody List<UUID> uuids);

    @Operation(summary = "Sync CBOMs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "CBOMs synced"),
            @ApiResponse(responseCode = "500", description = "Internal problem with repository",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PostMapping(path = "/sync")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void sync() throws CbomRepositoryException;

    @Operation(operationId = "listCbomSyncSkips", summary = "List the CBOM Repository entries the sync could not store",
            description = """
                    The documents the header sync found in the CBOM Repository listing but could not turn into a CBOM record: \
                    the document could not be read, or storing its header failed. Later sync runs retry an entry until its \
                    retry budget (the platform setting `cbomSyncSkippedRetryRuns`) is spent; it is then kept as permanently \
                    skipped until the retention (`cbomSyncSkipRetentionDays`) after its last attempt runs out. Each row \
                    carries the reason of the last failure, worded for an operator.

                    By default the list is ordered newest failure first: last attempt descending, then UUID ascending. \
                    `sort` reorders it and names its field by source `property` and identifier `CBOM_SYNC_SKIP_LAST_ATTEMPT_AT`, \
                    `CBOM_SYNC_SKIP_FIRST_SKIPPED_AT`, `CBOM_SYNC_SKIP_ATTEMPTS` or `CBOM_SYNC_SKIP_SERIAL_NUMBER`. `filters` \
                    may use `CBOM_SYNC_SKIP_STATE` (equals, not equals) and `CBOM_SYNC_SKIP_SERIAL_NUMBER` (equals, not \
                    equals, contains, not contains, starts with), both of source `property`, as the searchable-fields \
                    operation of this list publishes them. The rows have a fixed shape: no field is offered as a column, and \
                    `columns` is accepted and ignored.""")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of the entries the sync could not store"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(path = "/syncSkips", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    PaginationResponseDto<CbomSyncSkipDto> listSyncSkips(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(implementation = SearchRequestDto.class),
                    examples = {@ExampleObject(name = "Written-off entries, oldest first", value = """
                            {
                              "pageNumber": 1,
                              "itemsPerPage": 10,
                              "filters": [
                                {"fieldSource": "property", "fieldIdentifier": "CBOM_SYNC_SKIP_STATE",
                                 "condition": "EQUALS", "value": ["permanentlySkipped"]}
                              ],
                              "sort": {"fieldSource": "property", "fieldIdentifier": "CBOM_SYNC_SKIP_LAST_ATTEMPT_AT",
                                       "direction": "asc"}
                            }""")})) @Valid @RequestBody SearchRequestDto request);

    @Operation(operationId = "getCbomSyncSkipSearchableFields",
            summary = "Get the searchable fields of the entries the sync could not store",
            description = """
                    The fields the list of entries the sync could not store may be filtered by, each with the conditions it \
                    accepts and whether the list may also be ordered by it. The ordering keys that take no filter (last \
                    attempt, first failure, attempts) are named on the list operation and are not part of this catalogue. \
                    The rows have a fixed shape, so no field is offered as a column: `displayable` is never set.""")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Searchable field information retrieved")})
    @GetMapping(path = "/syncSkips/search", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<SearchFieldDataByGroupDto> getSyncSkipSearchableFields();

    @Operation(operationId = "retryCbomSyncSkip", summary = "Ask the sync to try a permanently skipped entry again",
            description = """
                    Puts a permanently skipped entry back to retrying with a full budget: `state` becomes `retrying` and \
                    `attempts` becomes 0, while `reason`, `firstSkippedAt` and `lastAttemptAt` keep the values of the last \
                    attempt until the next sync run tries the entry. An entry that is still retrying is left as it is. Answers \
                    with the row as it now stands.""")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The entry as it now stands"),
            @ApiResponse(responseCode = "404", description = "No such entry",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PostMapping(path = "/syncSkips/{uuid}/retry", produces = {MediaType.APPLICATION_JSON_VALUE})
    CbomSyncSkipDto retrySyncSkip(@Parameter(description = "Sync skip record UUID") @PathVariable UUID uuid)
            throws NotFoundException;

    @Operation(operationId = "getCbomSearchableFields", summary = "Get Cbom searchable fields information",
            description = ConfigurableColumnsDocs.CATALOGUE_FLAGS)
    @ApiResponses(
            value = {@ApiResponse(responseCode = "200", description = "Cbom searchable field information retrieved")})
    @GetMapping(path = "/search", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<SearchFieldDataByGroupDto> getSearchableFieldInformation();
}
