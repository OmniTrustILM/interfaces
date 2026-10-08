package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.certificate.SearchRequestDto;
import com.otilm.api.model.common.BulkActionMessageDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.common.PaginationResponseDto;
import com.otilm.api.model.core.search.ConfigurableColumnsDocs;
import com.otilm.api.model.core.search.SearchFieldDataByGroupDto;
import com.otilm.api.model.core.signing.signingrecord.SigningRecordDto;
import com.otilm.api.model.core.signing.signingrecord.SigningRecordListDto;
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

@RequestMapping("/v1/signingRecords")
@Tag(name = "Signing Record Management", description = "Signing Record Management API. "
        + "Signing Records are produced as a result of digital signing cryptographic operations. They cannot be created or updated directly via this API.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Not Found",
                content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
public interface SigningRecordController extends AuthProtectedController {

    @Operation(operationId = "listSigningRecordSearchableFields", summary = "List search filters for Signing Records",
            description = ConfigurableColumnsDocs.CATALOGUE_FLAGS)
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of search filters retrieved")})
    @GetMapping(path = "/search", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<SearchFieldDataByGroupDto> getSearchableFieldInformation();

    @Operation(operationId = "listSigningRecords", summary = "List of Signing Records",
            description = ConfigurableColumnsDocs.SORT_AND_COLUMNS + ConfigurableColumnsDocs.ATTRIBUTE_PROJECTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signing Records retrieved"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Content",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    PaginationResponseDto<SigningRecordListDto> listSigningRecords(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    schema = @Schema(implementation = SearchRequestDto.class),
                    examples = {
                            @ExampleObject(name = "With ordering and columns",
                                    value = """
                                            {
                                              "pageNumber": 1,
                                              "itemsPerPage": 10,
                                              "filters": [],
                                              "sort": {"fieldSource": "property", "fieldIdentifier": "SIGNING_RECORD_SIGNING_TIME", "direction": "desc"},
                                              "columns": [
                                                {"fieldSource": "property", "fieldIdentifier": "SIGNING_RECORD_NAME"},
                                                {"fieldSource": "property", "fieldIdentifier": "SIGNING_RECORD_SIGNING_PROFILE"},
                                                {"fieldSource": "property", "fieldIdentifier": "SIGNING_RECORD_PROTOCOL"}
                                              ]
                                            }""")})) @Valid @RequestBody SearchRequestDto request);

    @Operation(operationId = "getSigningRecord", summary = "Details of a Signing Record")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Signing Record details retrieved")})
    @GetMapping(path = "/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    SigningRecordDto getSigningRecord(@Parameter(description = "Signing Record UUID") @PathVariable UUID uuid)
            throws NotFoundException;

    @Operation(operationId = "deleteSigningRecord", summary = "Delete Signing Record")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Signing Record deleted")})
    @DeleteMapping(path = "/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteSigningRecord(@Parameter(description = "Signing Record UUID") @PathVariable UUID uuid)
            throws NotFoundException;

    @Operation(operationId = "bulkDeleteSigningRecords", summary = "Delete multiple Signing Records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signing Records deleted"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Content",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @DeleteMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BulkActionMessageDto> bulkDeleteSigningRecords(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Signing Record UUIDs",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {
                    @ExampleObject(
                            value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<UUID> uuids);
}
