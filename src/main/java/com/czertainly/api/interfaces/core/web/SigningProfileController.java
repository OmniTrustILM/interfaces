package com.czertainly.api.interfaces.core.web;

import com.czertainly.api.exception.AlreadyExistException;
import com.czertainly.api.exception.AttributeException;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.NotFoundException;
import com.czertainly.api.interfaces.AuthProtectedController;
import com.czertainly.api.model.client.approvalprofile.ApprovalProfileDto;
import com.czertainly.api.model.client.certificate.SearchRequestDto;
import com.czertainly.api.model.client.signing.profile.SigningProfileRequestDto;
import com.czertainly.api.model.client.signing.profile.workflow.SigningWorkflowType;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.common.attribute.common.DataAttribute;
import com.czertainly.api.model.core.certificate.CertificateDto;
import com.czertainly.api.model.core.signing.SigningProtocol;
import com.czertainly.api.model.common.BulkActionMessageDto;
import com.czertainly.api.model.common.ErrorMessageDto;
import com.czertainly.api.model.common.PaginationResponseDto;
import com.czertainly.api.model.core.search.SearchFieldDataByGroupDto;
import com.czertainly.api.model.client.signing.profile.SigningProfileDto;
import com.czertainly.api.model.client.signing.profile.SigningProfileListDto;
import com.czertainly.api.model.client.signing.protocols.tsp.TspActivationDetailDto;
import com.czertainly.api.model.core.signing.signingrecord.SigningRecordListDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.UUID;

@RequestMapping("/v1/signingProfiles")
@Tag(name = "Signing Profile Management", description = "Signing Profile Management API")
@ApiResponses(value = {@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
public interface SigningProfileController extends AuthProtectedController {

    @Operation(operationId = "listSigningProfileSearchableFields", summary = "List search filters for Signing Profiles")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of search filters retrieved")})
    @GetMapping(path = "/search", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<SearchFieldDataByGroupDto> getSearchableFieldInformation();

    @Operation(operationId = "listSupportedProtocols", summary = "List signing protocols supported for a given workflow type")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Supported protocols retrieved")})
    @GetMapping(path = "/supportedProtocols", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<SigningProtocol> listSupportedProtocols(
            @Parameter(description = "Signing workflow type code (e.g. 'timestamping')", required = true, schema = @Schema(implementation = SigningWorkflowType.class))
            @RequestParam String workflowType);

    @Operation(operationId = "listSigningProfiles", summary = "List of available Signing Profiles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signing Profiles retrieved"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))
    })
    @PostMapping(path = "/list", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    PaginationResponseDto<SigningProfileListDto> listSigningProfiles(@RequestBody SearchRequestDto request);

    @Operation(operationId = "getSigningProfile", summary = "Details of a Signing Profile. If no specific version is provided, the latest version will be returned.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Signing Profile details retrieved")})
    @GetMapping(path = "/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    SigningProfileDto getSigningProfile(@Parameter(description = "Signing Profile UUID") @PathVariable UUID uuid,
                                        @Parameter(in = ParameterIn.QUERY, description = "Specific version of the Signing Profile") @RequestParam(required = false) Integer version) throws NotFoundException;

    @Operation(operationId = "createSigningProfile", summary = "Add new Signing Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New Signing Profile added"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))
    })
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    SigningProfileDto createSigningProfile(@RequestBody @Valid SigningProfileRequestDto request) throws AlreadyExistException, AttributeException, NotFoundException;

    @Operation(operationId = "updateSigningProfile", summary = "Update Signing Profile", description = """
            Request to update an existing Signing Profile.
            If there are existing Digital Signatures produced using this Signing Profile, creates a new version of Signing Profile.
            Otherwise updates the latest version in-place.""")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signing Profile updated"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @PutMapping(path = "/{uuid}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    SigningProfileDto updateSigningProfile(@Parameter(description = "Signing Profile UUID") @PathVariable UUID uuid, @RequestBody @Valid SigningProfileRequestDto request)
            throws AlreadyExistException, AttributeException, NotFoundException;

    @Operation(operationId = "deleteSigningProfile", summary = "Delete Signing Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Signing Profile deleted")})
    @DeleteMapping(path = "/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteSigningProfile(@Parameter(description = "Signing Profile UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "bulkDeleteSigningProfiles", summary = "Delete multiple Signing Profiles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signing Profiles deleted"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @DeleteMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BulkActionMessageDto> bulkDeleteSigningProfiles(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Signing Profile UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<UUID> uuids);

    @Operation(operationId = "enableSigningProfile", summary = "Enable Signing Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Signing Profile enabled")})
    @PatchMapping(path = "/{uuid}/enable", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void enableSigningProfile(@Parameter(description = "Signing Profile UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "bulkEnableSigningProfiles", summary = "Enable multiple Signing Profiles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signing Profiles enabled"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))
    })
    @PatchMapping(path = "/enable", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BulkActionMessageDto> bulkEnableSigningProfiles(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Signing Profile UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<UUID> uuids);

    @Operation(operationId = "disableSigningProfile", summary = "Disable Signing Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Signing Profile disabled")})
    @PatchMapping(path = "/{uuid}/disable", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void disableSigningProfile(@Parameter(description = "Signing Profile UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "bulkDisableSigningProfiles", summary = "Disable multiple Signing Profiles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signing Profiles disabled"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))
    })
    @PatchMapping(path = "/disable", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BulkActionMessageDto> bulkDisableSigningProfiles(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Signing Profile UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<UUID> uuids);

    @Operation(operationId = "getAssociatedApprovalProfiles", summary = "List of Approval Profiles associated with the Signing Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Approval Profiles retrieved"),
            @ApiResponse(responseCode = "404", description = "Signing Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping(path = "/{uuid}/approvalProfiles", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<ApprovalProfileDto> getAssociatedApprovalProfiles(@Parameter(description = "Signing Profile UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "associateWithApprovalProfile", summary = "Associate Signing Profile with the Approval Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Approval Profile associated with the Signing Profile"),
            @ApiResponse(responseCode = "404", description = "Signing Profile or Approval Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PatchMapping(path = "/{signingProfileUuid}/approvalProfiles/{approvalProfileUuid}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void associateWithApprovalProfile(@Parameter(description = "Signing Profile UUID") @PathVariable UUID signingProfileUuid,
                                      @Parameter(description = "Approval Profile UUID") @PathVariable UUID approvalProfileUuid) throws NotFoundException;

    @Operation(operationId = "disassociateFromApprovalProfile", summary = "Disassociate Signing Profile with the Approval Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Approval Profile disassociated from the the Signing Profile"),
            @ApiResponse(responseCode = "404", description = "Signing Profile or Approval Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @DeleteMapping(path = "/{signingProfileUuid}/approvalProfiles/{approvalProfileUuid}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void disassociateFromApprovalProfile(@Parameter(description = "Signing Profile UUID") @PathVariable UUID signingProfileUuid,
                                         @Parameter(description = "Approval Profile UUID") @PathVariable UUID approvalProfileUuid) throws NotFoundException;

    @Operation(operationId = "listSigningCertificates", summary = "Get list of certificates eligible to be used for digital signing")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of certificates retrieved")})
    @GetMapping(path = "/signingCertificates", produces = MediaType.APPLICATION_JSON_VALUE)
    List<CertificateDto> listSigningCertificates(
            @Parameter(description = "Signing Workflow Type") @RequestParam SigningWorkflowType signingWorkflowType,
            @Parameter(description = "When true and signingWorkflowType is TIMESTAMPING, restricts results to certificates that satisfy ETSI EN 319 421 qualified timestamp requirements")
            @RequestParam(required = false, defaultValue = "false") boolean qualifiedTimestamp);

    @Operation(
            operationId = "listSignatureAttributesForCertificate",
            summary = "Get signing operation attribute descriptors for a certificate",
            description = "Returns the signing operation attribute descriptors (e.g. signature scheme, digest algorithm) " +
                    "derived from the key algorithm of the given certificate. " +
                    "Intended for use during Signing Profile creation to populate the signingOperationAttributes field."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signature attribute descriptors retrieved"),
            @ApiResponse(responseCode = "404", description = "Certificate not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping(path = "/certificates/{certificateUuid}/signatureAttributes", produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listSignatureAttributesForCertificate(
            @Parameter(description = "Certificate UUID") @PathVariable UUID certificateUuid) throws NotFoundException;

    @Operation(
            operationId = "listSignatureFormatterConnectorAttributes",
            summary = "Get formatter attribute descriptors from a Signature Formatter Connector",
            description = "Queries the Signature Formatter Connector for its available formatter attribute descriptors with connector default values. " +
                    "The signingProfileUuid parameter is used for authorization only and does not affect the returned descriptors."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Formatter attribute descriptors retrieved"),
            @ApiResponse(responseCode = "404", description = "Connector or Signing Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping(path = "/signatureFormatterConnectors/{connectorUuid}/formatterAttributes", produces = MediaType.APPLICATION_JSON_VALUE)
    List<DataAttribute> listSignatureFormatterConnectorAttributes(
            @Parameter(description = "Signature Formatter Connector UUID") @PathVariable UUID connectorUuid,
            @Parameter(description = "Signing Profile UUID — used for authorization purposes only", in = ParameterIn.QUERY) @RequestParam(required = false) UUID signingProfileUuid) throws NotFoundException, ConnectorException, AttributeException;

    // -----------------------------------------------------------------------------------------------------------------
    // Signing Records
    // -----------------------------------------------------------------------------------------------------------------

    @Operation(
            operationId = "listSigningRecordsForSigningProfile",
            summary = "List Signing Records produced under a Signing Profile",
            description = "Returns a paginated, filterable list of all Signing Records that were produced " +
                    "using this Signing Profile. Supports the same search and pagination parameters as " +
                    "the top-level Signing Records listing."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signing Records retrieved"),
            @ApiResponse(responseCode = "404", description = "Signing Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))
    })

    @PostMapping(path = "/{uuid}/signingRecords", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    PaginationResponseDto<SigningRecordListDto> listSigningRecordsForSigningProfile(
            @Parameter(description = "Signing Profile UUID") @PathVariable UUID uuid,
            @RequestBody SearchRequestDto request
    ) throws NotFoundException;

// -----------------------------------------------------------------------------------------------------------------
// Protocols
// -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Get the activation details of the Timestamping Protocol (TSP) for Signing Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "TSP details retrieved"),
            @ApiResponse(responseCode = "404", description = "Signing Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping(path = "/{uuid}/protocols/tsp", produces = {MediaType.APPLICATION_JSON_VALUE})
    TspActivationDetailDto getTspActivationDetails(@Parameter(description = "Signing Profile UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "activateTsp", summary = "Activate TSP for Signing Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TSP activated", content = @Content(schema = @Schema(implementation = TspActivationDetailDto.class))),
            @ApiResponse(responseCode = "404", description = "Signing Profile or TSP Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PatchMapping(path = "/{signingProfileUuid}/protocols/tsp/activate/{tspProfileUuid}", produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    TspActivationDetailDto activateTsp(@Parameter(description = "Signing Profile UUID") @PathVariable UUID signingProfileUuid,
                                       @Parameter(description = "TSP Profile UUID") @PathVariable UUID tspProfileUuid) throws NotFoundException;

    @Operation(operationId = "deactivateTsp", summary = "Deactivate TSP for Signing Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "TSP deactivated"),
            @ApiResponse(responseCode = "404", description = "Signing Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PatchMapping(path = "/{uuid}/protocols/tsp/deactivate", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deactivateTsp(@Parameter(description = "Signing Profile UUID") @PathVariable UUID uuid) throws NotFoundException;
}
