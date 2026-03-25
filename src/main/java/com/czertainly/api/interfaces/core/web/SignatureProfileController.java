package com.czertainly.api.interfaces.core.web;

import com.czertainly.api.exception.AlreadyExistException;
import com.czertainly.api.exception.AttributeException;
import com.czertainly.api.exception.NotFoundException;
import com.czertainly.api.interfaces.AuthProtectedController;
import com.czertainly.api.model.client.approvalprofile.ApprovalProfileDto;
import com.czertainly.api.model.client.certificate.SearchRequestDto;
import com.czertainly.api.model.common.BulkActionMessageDto;
import com.czertainly.api.model.common.ErrorMessageDto;
import com.czertainly.api.model.common.PaginationResponseDto;
import com.czertainly.api.model.core.search.SearchFieldDataByGroupDto;
import com.czertainly.api.model.core.signing.signatureprofile.SignatureProfileCreateRequestDto;
import com.czertainly.api.model.core.signing.signatureprofile.SignatureProfileDto;
import com.czertainly.api.model.core.signing.signatureprofile.SignatureProfileListDto;
import com.czertainly.api.model.core.signing.signatureprofile.SignatureProfileUpdateRequestDto;
import com.czertainly.api.model.core.signing.digitalsignature.DigitalSignatureListDto;
import com.czertainly.api.model.core.signing.tsp.TspConfigurationDto;
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

@RequestMapping("/v1/signatureProfiles")
@Tag(name = "Signature Profile Management", description = "Signature Profile Management API")
@ApiResponses(value = {@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
public interface SignatureProfileController extends AuthProtectedController {

    @Operation(operationId = "listSignatureProfileSearchableFields", summary = "List search filters for Signature Profiles")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of search filters retrieved")})
    @GetMapping(path = "/search", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<SearchFieldDataByGroupDto> getSearchableFieldInformation();

    @Operation(operationId = "listSignatureProfiles", summary = "List of available Signature Profiles")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Signature Profiles retrieved")})
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    PaginationResponseDto<SignatureProfileListDto> listSignatureProfiles(@RequestBody SearchRequestDto request);

    @Operation(operationId = "getSignatureProfile", summary = "Details of a Signature Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Signature Profile details retrieved")})
    @GetMapping(path = "/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    SignatureProfileDto getSignatureProfile(@Parameter(description = "Signature Profile UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "createSignatureProfile", summary = "Add new Signature Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New Signature Profile added"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    SignatureProfileDto createSignatureProfile(@RequestBody @Valid SignatureProfileCreateRequestDto request) throws AlreadyExistException, AttributeException, NotFoundException;

    @Operation(operationId = "updateSignatureProfile", summary = "Update Signature Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signature Profile updated"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @PutMapping(path = "/{uuid}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    SignatureProfileDto updateSignatureProfile(@Parameter(description = "Signature Profile UUID") @PathVariable UUID uuid, @RequestBody @Valid SignatureProfileUpdateRequestDto request) throws NotFoundException, AttributeException;

    @Operation(operationId = "deleteSignatureProfile", summary = "Delete Signature Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Signature Profile deleted")})
    @DeleteMapping(path = "/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteSignatureProfile(@Parameter(description = "Signature Profile UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "bulkDeleteSignatureProfiles", summary = "Delete multiple Signature Profiles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signature Profiles deleted"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @DeleteMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BulkActionMessageDto> bulkDeleteSignatureProfiles(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Signature Profile UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<UUID> uuids);

    @Operation(operationId = "enableSignatureProfile", summary = "Enable Signature Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Signature Profile enabled")})
    @PatchMapping(path = "/{uuid}/enable", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void enableSignatureProfile(@Parameter(description = "Signature Profile UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "bulkEnableSignatureProfiles", summary = "Enable multiple Signature Profiles")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Signature Profiles enabled")})
    @PatchMapping(path = "/enable", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BulkActionMessageDto> bulkEnableSignatureProfiles(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Signature Profile UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<UUID> uuids);

    @Operation(operationId = "disableSignatureProfile", summary = "Disable Signature Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Signature Profile disabled")})
    @PatchMapping(path = "/{uuid}/disable", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void disableSignatureProfile(@Parameter(description = "Signature Profile UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "bulkDisableSignatureProfiles", summary = "Disable multiple Signature Profiles")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Signature Profiles disabled")})
    @PatchMapping(path = "/disable", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BulkActionMessageDto> bulkDisableSignatureProfiles(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Signature Profile UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<UUID> uuids);

    @Operation(operationId = "getAssociatedApprovalProfiles", summary = "List of Approval Profiles associated with the Signature Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Approval Profiles retrieved"),
            @ApiResponse(responseCode = "404", description = "Signature Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping(path = "/{uuid}/approvalProfiles", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<ApprovalProfileDto> getAssociatedApprovalProfiles(@Parameter(description = "Signature Profile UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "associateWithApprovalProfile", summary = "Associate Signature Profile with the Approval Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Approval Profile associated with the Signature Profile"),
            @ApiResponse(responseCode = "404", description = "Signature Profile or Approval Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PatchMapping(path = "/{signatureProfileUuid}/approvalProfiles/{approvalProfileUuid}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void associateWithApprovalProfile(@Parameter(description = "Signature Profile UUID") @PathVariable UUID signatureProfileUuid,
                                      @Parameter(description = "Approval Profile UUID") @PathVariable UUID approvalProfileUuid) throws NotFoundException;

    @Operation(operationId = "disassociateFromApprovalProfile", summary = "Disassociate Signature Profile with the Approval Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Approval Profile disassociated from the the Signature Profile"),
            @ApiResponse(responseCode = "404", description = "Signature Profile or Approval Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @DeleteMapping(path = "/{signatureProfileUuid}/approvalProfiles/{approvalProfileUuid}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void disassociateFromApprovalProfile(@Parameter(description = "Signature Profile UUID") @PathVariable UUID signatureProfileUuid,
                                         @Parameter(description = "Approval Profile UUID") @PathVariable UUID approvalProfileUuid) throws NotFoundException;


    // -----------------------------------------------------------------------------------------------------------------
    // Digital Signatures
    // -----------------------------------------------------------------------------------------------------------------

    @Operation(
            operationId = "listDigitalSignaturesForSignatureProfile",
            summary = "List Digital Signatures produced under a Signature Profile",
            description = "Returns a paginated, filterable list of all Digital Signatures that were produced " +
                    "using this Signature Profile. Supports the same search and pagination parameters as " +
                    "the top-level Digital Signatures listing."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Digital Signatures retrieved"),
            @ApiResponse(responseCode = "404", description = "Signature Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PostMapping(path = "/{uuid}/digitalSignatures", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    PaginationResponseDto<DigitalSignatureListDto> listDigitalSignaturesForSignatureProfile(
            @Parameter(description = "Signature Profile UUID") @PathVariable UUID uuid,
            @RequestBody SearchRequestDto request
    ) throws NotFoundException;

    // -----------------------------------------------------------------------------------------------------------------
    // Protocols
    // -----------------------------------------------------------------------------------------------------------------

    @Operation(operationId = "activateIlmSigningProtocol", summary = "Activate ILM Signing Protocol for Signature Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "ILM Signing Protocol activated"),
            @ApiResponse(responseCode = "404", description = "Signature Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PatchMapping(
            path = "/{signatureProfileUuid}/protocols/ilm/activate/{ilmSigningProtocolConfigurationUuid}",
            consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void activateIlmSigningProtocol(@Parameter(description = "Signature Profile UUID") @PathVariable UUID signatureProfileUuid,
                                    @Parameter(description = "ILM Signing Protocol Configuration UUID") @PathVariable UUID ilmSigningProtocolConfigurationUuid) throws NotFoundException;

    @Operation(operationId = "deactivateIlmSigningProtocol", summary = "Deactivate ILM Signing Protocol for Signature Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "ILM Signing Protocol deactivated"),
            @ApiResponse(responseCode = "404", description = "Signature Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PatchMapping(path = "/{uuid}/protocols/ilm/deactivate", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deactivateIlmSigningProtocol(@Parameter(description = "Signature Profile UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "activateTsp", summary = "Activate TSP for Signature Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "TSP activated"),
            @ApiResponse(responseCode = "404", description = "Signature Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PatchMapping(
            path = "/{signatureProfileUuid}/protocols/tsp/activate/{tspConfigurationUuid}",
            consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)

    void activateTsp(@Parameter(description = "Signature Profile UUID") @PathVariable UUID signatureProfileUuid,
                     @Parameter(description = "TSP Configuration UUID") @PathVariable UUID tspConfigurationUuid) throws NotFoundException;

    @Operation(operationId = "deactivateTsp", summary = "Deactivate TSP for Signature Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "TSP deactivated"),
            @ApiResponse(responseCode = "404", description = "Signature Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PatchMapping(path = "/{uuid}/protocols/tsp/deactivate", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deactivateTsp(@Parameter(description = "Signature Profile UUID") @PathVariable UUID uuid) throws NotFoundException;
}
