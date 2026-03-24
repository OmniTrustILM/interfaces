package com.czertainly.api.interfaces.core.web;

import com.czertainly.api.exception.AlreadyExistException;
import com.czertainly.api.exception.AttributeException;
import com.czertainly.api.exception.NotFoundException;
import com.czertainly.api.interfaces.AuthProtectedController;
import com.czertainly.api.model.client.certificate.SearchRequestDto;
import com.czertainly.api.model.common.BulkActionMessageDto;
import com.czertainly.api.model.common.ErrorMessageDto;
import com.czertainly.api.model.common.PaginationResponseDto;
import com.czertainly.api.model.core.search.SearchFieldDataByGroupDto;
import com.czertainly.api.model.core.signing.workflow.SigningWorkflowConfigurationCreateRequestDto;
import com.czertainly.api.model.core.signing.workflow.SigningWorkflowConfigurationDto;
import com.czertainly.api.model.core.signing.workflow.SigningWorkflowConfigurationListDto;
import com.czertainly.api.model.core.signing.workflow.SigningWorkflowConfigurationUpdateRequestDto;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.UUID;

@RequestMapping("/v1/signingWorkflowConfigurations")
@Tag(name = "Signing Workflow Configuration Management", description = "Signing Workflow Configuration Management API")
@ApiResponses(value = {@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
public interface SigningWorkflowConfigurationController extends AuthProtectedController {

    @Operation(operationId = "listSigningWorkflowConfigurationSearchableFields", summary = "List search filters for Signing Workflow Configurations")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of search filters retrieved")})
    @GetMapping(path = "/search", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<SearchFieldDataByGroupDto> getSearchableFieldInformation();

    @Operation(operationId = "listSigningWorkflowConfigurations", summary = "List of available Signing Workflow Configurations")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Signing Workflow Configurations retrieved")})
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    PaginationResponseDto<SigningWorkflowConfigurationListDto> listSigningWorkflowConfigurations(@RequestBody SearchRequestDto request);

    @Operation(operationId = "getSigningWorkflowConfiguration", summary = "Details of a Signing Workflow Configuration")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Signing Workflow Configuration details retrieved")})
    @GetMapping(path = "/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    SigningWorkflowConfigurationDto getSigningWorkflowConfiguration(@Parameter(description = "Signing Workflow Configuration UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "createSigningWorkflowConfiguration", summary = "Add new Signing Workflow Configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New Signing Workflow Configuration added"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    SigningWorkflowConfigurationDto createSigningWorkflowConfiguration(@RequestBody @Valid SigningWorkflowConfigurationCreateRequestDto request) throws AlreadyExistException, AttributeException, NotFoundException;

    @Operation(operationId = "updateSigningWorkflowConfiguration", summary = "Update Signing Workflow Configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signing Workflow Configuration updated"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @PutMapping(path = "/{uuid}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    SigningWorkflowConfigurationDto updateSigningWorkflowConfiguration(@Parameter(description = "Signing Workflow Configuration UUID") @PathVariable UUID uuid, @RequestBody @Valid SigningWorkflowConfigurationUpdateRequestDto request) throws NotFoundException, AttributeException;

    @Operation(operationId = "deleteSigningWorkflowConfiguration", summary = "Delete Signing Workflow Configuration")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Signing Workflow Configuration deleted")})
    @DeleteMapping(path = "/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteSigningWorkflowConfiguration(@Parameter(description = "Signing Workflow Configuration UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "bulkDeleteSigningWorkflowConfigurations", summary = "Delete multiple Signing Workflow Configurations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signing Workflow Configurations deleted"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @DeleteMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BulkActionMessageDto> bulkDeleteSigningWorkflowConfigurations(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Signing Workflow Configuration UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<UUID> uuids);
}
