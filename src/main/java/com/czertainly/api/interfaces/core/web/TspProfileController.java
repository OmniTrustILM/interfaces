package com.czertainly.api.interfaces.core.web;

import com.czertainly.api.exception.AlreadyExistException;
import com.czertainly.api.exception.AttributeException;
import com.czertainly.api.exception.NotFoundException;
import com.czertainly.api.interfaces.AuthProtectedController;
import com.czertainly.api.model.client.certificate.SearchRequestDto;
import com.czertainly.api.model.client.signing.protocols.tsp.TspProfileDto;
import com.czertainly.api.model.client.signing.protocols.tsp.TspProfileListDto;
import com.czertainly.api.model.client.signing.protocols.tsp.TspProfileRequestDto;
import com.czertainly.api.model.common.BulkActionMessageDto;
import com.czertainly.api.model.common.ErrorMessageDto;
import com.czertainly.api.model.common.PaginationResponseDto;
import com.czertainly.api.model.core.search.SearchFieldDataByGroupDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.UUID;

@RequestMapping("/v1/tspProfiles")
@Tag(name = "TSP Profile Management", description = "Timestamping Protocol (TSP) Profile Management API")
@ApiResponses(value = {@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
public interface TspProfileController extends AuthProtectedController {

    @Operation(operationId = "listTspProfileSearchableFields", summary = "List search filters for TSP Profiles")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of search filters retrieved")})
    @GetMapping(path = "/search", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<SearchFieldDataByGroupDto> getSearchableFieldInformation();

    @Operation(operationId = "listTspProfiles", summary = "List of available TSP Profiles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TSP Profiles retrieved"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))
    })
    @PostMapping(path = "/list", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    PaginationResponseDto<TspProfileListDto> listTspProfiles(@RequestBody SearchRequestDto request);

    @Operation(operationId = "getTspProfile", summary = "Details of a TSP Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "TSP Profile details retrieved")})
    @GetMapping(path = "/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    TspProfileDto getTspProfile(@Parameter(description = "TSP Profile UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "createTspProfile", summary = "Add new TSP Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New TSP Profile added"),
            @ApiResponse(responseCode = "409", description = "Already Exists", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    TspProfileDto createTspProfile(@RequestBody @Valid TspProfileRequestDto request) throws AlreadyExistException, AttributeException, NotFoundException;

    @Operation(operationId = "updateTspProfile", summary = "Update TSP Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TSP Profile updated"),
            @ApiResponse(responseCode = "409", description = "Already Exists", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @PutMapping(path = "/{uuid}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    TspProfileDto updateTspProfile(@Parameter(description = "TSP Profile UUID") @PathVariable UUID uuid, @RequestBody @Valid TspProfileRequestDto request)
            throws AlreadyExistException, AttributeException, NotFoundException;

    @Operation(operationId = "deleteTspProfile", summary = "Delete TSP Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "TSP Profile deleted")})
    @DeleteMapping(path = "/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteTspProfile(@Parameter(description = "TSP Profile UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "bulkDeleteTspProfiles", summary = "Delete multiple TSP Profiles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TSP Profiles deleted"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @DeleteMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BulkActionMessageDto> bulkDeleteTspProfiles(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "TSP Profile UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<UUID> uuids);

    @Operation(operationId = "enableTspProfile", summary = "Enable TSP Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "TSP Profile enabled")})
    @PatchMapping(path = "/{uuid}/enable", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void enableTspProfile(@Parameter(description = "TSP Profile UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "bulkEnableTspProfiles", summary = "Enable multiple TSP Profiles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TSP Profiles enabled"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))
    })
    @PatchMapping(path = "/enable", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BulkActionMessageDto> bulkEnableTspProfiles(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "TSP Profile UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<UUID> uuids);

    @Operation(operationId = "disableTspProfile", summary = "Disable TSP Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "TSP Profile disabled")})
    @PatchMapping(path = "/{uuid}/disable", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void disableTspProfile(@Parameter(description = "TSP Profile UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "bulkDisableTspProfiles", summary = "Disable multiple TSP Profiles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TSP Profiles disabled"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))
    })
    @PatchMapping(path = "/disable", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BulkActionMessageDto> bulkDisableTspProfiles(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "TSP Profile UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<UUID> uuids);
}
