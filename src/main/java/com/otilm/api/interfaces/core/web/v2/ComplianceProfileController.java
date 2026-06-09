package com.otilm.api.interfaces.core.web.v2;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.AttributeException;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.compliance.v2.*;
import com.otilm.api.model.common.BulkActionMessageDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.compliance.v2.ComplianceGroupListDto;
import com.otilm.api.model.core.compliance.v2.ComplianceProfileDto;
import com.otilm.api.model.core.compliance.v2.ComplianceProfileListDto;
import com.otilm.api.model.core.compliance.v2.ComplianceRuleListDto;
import com.otilm.api.model.core.other.ResourceObjectDto;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/v2/complianceProfiles")
@Tag(name = "Compliance Profile Management v2", description = "Compliance Profile Management v2 API")
@ApiResponses(value = {@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))), @ApiResponse(responseCode = "502", description = "Connector Error", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))), @ApiResponse(responseCode = "503", description = "Connector Communication Error", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
public interface ComplianceProfileController extends AuthProtectedController {

    @Operation(operationId = "listComplianceProfilesV2", summary = "List of available Compliance Profiles")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Compliance Profiles retrieved")})
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    List<ComplianceProfileListDto> listComplianceProfiles();

    @Operation(operationId = "getComplianceProfileV2", summary = "Details of a Compliance Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Compliance Profile details retrieved")})
    @GetMapping(path = "/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    ComplianceProfileDto getComplianceProfile(@Parameter(description = "Compliance Profile UUID") @PathVariable UUID uuid) throws ConnectorException, NotFoundException;

    @Operation(operationId = "createComplianceProfileV2", summary = "Add Compliance Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "New Compliance profile added"), @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    ComplianceProfileDto createComplianceProfile(@RequestBody @Valid ComplianceProfileRequestDto request) throws ConnectorException, NotFoundException, AlreadyExistException, AttributeException;

    @Operation(operationId = "updateComplianceProfileV2", summary = "Update Compliance Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Compliance profile updated"), @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @PutMapping(path = "/{uuid}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    ComplianceProfileDto updateComplianceProfile(@Parameter(description = "Compliance Profile UUID") @PathVariable UUID uuid, @RequestBody @Valid ComplianceProfileUpdateRequestDto request) throws ConnectorException, NotFoundException, AttributeException;

    @Operation(operationId = "deleteComplianceProfileV2", summary = "Delete Compliance Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Compliance Profile deleted")})
    @DeleteMapping(path = "/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComplianceProfile(@Parameter(description = "Compliance Profile UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "bulkDeleteComplianceProfilesV2", summary = "Delete multiple Compliance Profiles")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Compliance Profiles deleted"), @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @DeleteMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BulkActionMessageDto> bulkDeleteComplianceProfiles(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Compliance Profile UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<UUID> uuids);

    @Operation(operationId = "forceDeleteComplianceProfilesV2", summary = "Force delete Compliance Profiles")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Compliance Profiles forced to delete"), @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @DeleteMapping(path = "/force", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BulkActionMessageDto> forceDeleteComplianceProfiles(@RequestBody List<UUID> uuids);

    @Operation(operationId = "getComplianceRulesV2", summary = "Get Compliance rules", description = "Lists compliance rules. If provider UUID is sent (also kind is required) then provider rules are listed, otherwise lists internal rules")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Compliance rules retrieved"), @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @GetMapping(path = "/rules", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<ComplianceRuleListDto> getComplianceRules(@RequestParam(required = false) UUID connectorUuid, @RequestParam(required = false) String kind, @RequestParam(required = false) Resource resource, @RequestParam(required = false) String type, @RequestParam(required = false) String format) throws ConnectorException, NotFoundException;

    @Operation(operationId = "getComplianceGroupsV2", summary = "Get Compliance groups")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Compliance groups retrieved"), @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @GetMapping(path = "/groups", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<ComplianceGroupListDto> getComplianceGroups(@RequestParam UUID connectorUuid, @RequestParam String kind, @RequestParam(required = false) Resource resource) throws ConnectorException, NotFoundException;

    @Operation(operationId = "getComplianceGroupRulesV2", summary = "Get Compliance group rules")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Compliance group rules retrieved"), @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @GetMapping(path = "/groups/{groupUuid}/rules", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<ComplianceRuleListDto> getComplianceGroupRules(@PathVariable UUID groupUuid, @RequestParam UUID connectorUuid, @RequestParam String kind) throws ConnectorException, NotFoundException;

    @Operation(operationId = "createComplianceInternalRuleV2", summary = "Create Compliance internal rule")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Compliance internal rule created"), @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @PostMapping(path = "/rules", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    ComplianceRuleListDto createComplianceInternalRule(@RequestBody @Valid ComplianceInternalRuleRequestDto request) throws AlreadyExistException;

    @Operation(operationId = "updateComplianceInternalRuleV2", summary = "Update Compliance internal rule")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Compliance internal rule updated"), @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @PutMapping(path = "/rules/{internalRuleUuid}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    ComplianceRuleListDto updateComplianceInternalRule(@Parameter(description = "Compliance internal rule UUID") @PathVariable UUID internalRuleUuid, @RequestBody @Valid ComplianceInternalRuleRequestDto request) throws NotFoundException;

    @Operation(operationId = "deleteComplianceInternalRuleV2", summary = "Delete Compliance internal rule")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Compliance internal rule deleted")})
    @DeleteMapping(path = "/rules/{internalRuleUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComplianceInternalRule(@Parameter(description = "Compliance internal rule UUID") @PathVariable UUID internalRuleUuid) throws NotFoundException;

    @Operation(operationId = "patchComplianceProfileRulesV2", summary = "Add/remove compliance rule to/from Compliance Profile", description = "If provider UUID is sent (also kind is required) then provider rules is handled, otherwise handling internal rule")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Rule added/removed to/from the profile"), @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @PatchMapping(path = "/{uuid}/rules", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void patchComplianceProfileRule(@Parameter(description = "Compliance Profile UUID") @PathVariable UUID uuid, @RequestBody @Valid ComplianceProfileRulesPatchRequestDto request) throws ConnectorException, NotFoundException;

    @Operation(operationId = "patchComplianceProfileGroupsV2", summary = "Add/remove group to/from Compliance Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Group is added/removed to/from the profile"), @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @PatchMapping(path = "/{uuid}/groups", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void patchComplianceProfileGroup(@Parameter(description = "Compliance Profile UUID") @PathVariable UUID uuid, @RequestBody @Valid ComplianceProfileGroupsPatchRequestDto request) throws ConnectorException, NotFoundException;

    @Operation(operationId = "getAssociationsV2", summary = "Get associations of Compliance Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Associated resource objects retrieved")})
    @GetMapping(path = "/{uuid}/associations", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<ResourceObjectDto> getAssociations(@Parameter(description = "Compliance Profile UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "associateComplianceProfileV2", summary = "Associate Compliance Profile to specified resource object")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Resource object association successful"), @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @PatchMapping(path = "/{uuid}/associations/{resource}/{associationObjectUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void associateComplianceProfile(@Parameter(description = "Compliance Profile UUID", required = true) @PathVariable UUID uuid, @Parameter(description = "Resource", required = true, example = Resource.Codes.RA_PROFILE) @PathVariable Resource resource, @Parameter(description = "Association object UUID", required = true) @PathVariable UUID associationObjectUuid) throws NotFoundException, AlreadyExistException;

    @Operation(operationId = "disassociateComplianceProfileV2", summary = "Disassociate Compliance Profile from specified resource object")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Resource object disassociation successful"), @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @DeleteMapping(path = "/{uuid}/associations/{resource}/{associationObjectUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void disassociateComplianceProfile(@Parameter(description = "Compliance Profile UUID", required = true) @PathVariable UUID uuid, @Parameter(description = "Resource", required = true, example = Resource.Codes.RA_PROFILE) @PathVariable Resource resource, @Parameter(description = "Association object UUID", required = true) @PathVariable UUID associationObjectUuid) throws NotFoundException;

    @Operation(operationId = "getAssociatedComplianceProfilesV2", summary = "Get associated Compliance Profiles for resource object")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Associated Compliance Profiles retrieved")})
    @GetMapping(path = "/associations/{resource}/{associationObjectUuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<ComplianceProfileListDto> getAssociatedComplianceProfiles(@Parameter(description = "Resource", required = true, example = Resource.Codes.RA_PROFILE) @PathVariable Resource resource, @Parameter(description = "Association object UUID", required = true) @PathVariable UUID associationObjectUuid);
}
