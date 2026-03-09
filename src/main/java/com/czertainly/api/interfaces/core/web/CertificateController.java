package com.czertainly.api.interfaces.core.web;

import com.czertainly.api.exception.*;
import com.czertainly.api.interfaces.AuthProtectedController;
import com.czertainly.api.interfaces.core.web.v2.ComplianceController;
import com.czertainly.api.model.client.approval.ApprovalResponseDto;
import com.czertainly.api.model.client.certificate.*;
import com.czertainly.api.model.common.ErrorMessageDto;
import com.czertainly.api.model.common.UuidDto;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.core.certificate.*;
import com.czertainly.api.model.core.location.LocationDto;
import com.czertainly.api.model.core.scheduler.PaginationRequestDto;
import com.czertainly.api.model.core.search.SearchFieldDataByGroupDto;
import com.czertainly.api.model.core.v2.ClientCertificateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.UUID;

@RequestMapping("/v1/certificates")
@Tag(name = "Certificate Inventory", description = "Certificate Inventory API")
@ApiResponses(
        value = {
                @ApiResponse(
                        responseCode = "404",
                        description = "Not Found",
                        content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
                )
        })
public interface CertificateController extends AuthProtectedController {

    @Operation(summary = "List Certificates")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of all the certificates")})
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    CertificateResponseDto listCertificates(@RequestBody CertificateSearchRequestDto request);

    @Operation(summary = "Get Certificate Details")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Certificate detail retrieved")})
    @GetMapping(path = "/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    CertificateDetailDto getCertificate(@Parameter(description = "Certificate UUID") @PathVariable UUID uuid)
            throws NotFoundException, CertificateException, IOException;

    @Operation(summary = "Download Certificate")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Certificate downloaded")})
    @GetMapping(path = "/{uuid}/{certificateFormat}", produces = {MediaType.APPLICATION_JSON_VALUE})
    CertificateDownloadResponseDto downloadCertificate(@Parameter(description = "Certificate UUID") @PathVariable UUID uuid, @Parameter(description = "Certificate format") @PathVariable CertificateFormat certificateFormat, @RequestParam CertificateFormatEncoding encoding)
            throws NotFoundException, CertificateException, IOException;

    @Operation(summary = "Delete a certificate")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Certificate deleted")})
    @DeleteMapping(path = "/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCertificate(@Parameter(description = "Certificate UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(summary = "Update Certificate Objects")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Certificate objects updated")})
    @PatchMapping(path = "/{uuid}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateCertificateObjects(@Parameter(description = "Certificate UUID") @PathVariable UUID uuid, @RequestBody CertificateUpdateObjectsDto request)
            throws NotFoundException, CertificateOperationException, ValidationException, AttributeException;

    @Operation(summary = "Update Group and/or Owner for multiple Certificates", description = "In this operation, when the list of " +
            "Certificate UUIDs are provided and the filter is left as null or undefined, then the change will " +
            "be applied only to the list of Certificate UUIDs provided. When the filter is provided in the request, " +
            "the list of UUIDs will be ignored and the change will be applied for the all the certificates that matches " +
            "the filter criteria. To apply this change for all the Certificates in the inventory, " +
            "provide an empty array \"[]\" for the value of \"filters\" in the request body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Certificate objects updated"),
            @ApiResponse(responseCode = "501", description = "Certificate objects update by filters not supported")}
    )
    @PatchMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void bulkUpdateCertificateObjects(@RequestBody MultipleCertificateObjectUpdateDto request)
            throws NotFoundException, NotSupportedException;

    @Operation(summary = "Upload a new Certificate")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Certificate uploaded", content = @Content(schema = @Schema(implementation = UuidDto.class)))})
    @PostMapping(path = "/upload", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<UuidDto> upload(@RequestBody UploadCertificateRequestDto request)
            throws AlreadyExistException, CertificateException, NoSuchAlgorithmException, NotFoundException, AttributeException;

    @Operation(summary = "Delete multiple certificates", description = "In this operation, when the list of " +
            "Certificate UUIDs are provided and the filter is left as null or undefined, then the change will " +
            "be applied only to the list of Certificate UUIDs provided. When the filter is provided in the request, " +
            "the list of UUIDs will be ignored and the change will be applied for the all the certificates that matches " +
            "the filter criteria. To apply this change for all the Certificates in the inventory, " +
            "provide an empty array \"[]\" for the value of \"filters\" in the request body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Certificates deleted"),
            @ApiResponse(responseCode = "501", description = "Certificate objects delete by filters not supported")
    })
    @PostMapping(path = "/delete", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    BulkOperationResponse bulkDeleteCertificate(@RequestBody RemoveCertificateDto request) throws NotFoundException, NotSupportedException;

    @Operation(operationId = "getCertificateSearchableFields", summary = "Get Certificate searchable fields information")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Certificate searchable field information retrieved")})
    @GetMapping(path = "/search", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<SearchFieldDataByGroupDto> getSearchableFieldInformation();

    @Operation(summary = "Get Certificate event history")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Certificate event history retrieved")})
    @GetMapping(path = "/{uuid}/history", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<CertificateEventHistoryDto> getCertificateEventHistory(@Parameter(description = "Certificate UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(
            summary = "List of available Locations for the Certificate",
            operationId = "listCertificateLocations"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Locations retrieved"
                    )
            })
    @GetMapping(
            path = "/{certificateUuid}/locations",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    List<LocationDto> listLocations(
            @Parameter(description = "Certificate UUID") @PathVariable UUID certificateUuid
    ) throws NotFoundException;

    /**
     * @deprecated As of release 2.16.0. Replaced by {@link ComplianceController#checkResourceObjectsCompliance} with resource Certificate.
     */
    @Deprecated(since = "2.16.0", forRemoval = true)
    @Operation(summary = "Initiate Certificate Compliance Check", operationId = "checkCertificatesCompliance", deprecated = true)
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Compliance check initiated")})
    @PostMapping(path = "/compliance", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void checkCompliance(@RequestBody CertificateComplianceCheckDto request) throws NotFoundException;

    @Operation(summary = "Get Certificate Validation Result")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Certificate validation detail retrieved")})
    @GetMapping(path = "/{uuid}/validate", produces = {MediaType.APPLICATION_JSON_VALUE})
    CertificateValidationResultDto getCertificateValidationResult(@Parameter(description = "Certificate UUID") @PathVariable UUID uuid) throws NotFoundException, CertificateException;

    @Operation(summary = "Get CSR Generation Attributes")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "CSR Generation attributes retrieved")})
    @GetMapping(path = "/csr/attributes", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BaseAttribute> getCsrGenerationAttributes();

    @Operation(summary = "Get Certificate Content")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Certificate content retrieved"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(path = "/content", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    List<CertificateContentDto> getCertificateContent(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Certificate UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")}))
                                                      @RequestBody List<UUID> uuids);

    @Operation(
            summary = "Submit certificate request"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Certificate request submit, certificate created and ready to be issued")
    })
    @PostMapping(
            path = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    CertificateDetailDto submitCertificateRequest(
            @RequestBody ClientCertificateRequestDto request
    ) throws ValidationException, ConnectorException, CertificateException, IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, AttributeException, CertificateRequestException, NotFoundException;

    @Operation(
            summary = "Get certificate chain",
            description = "Get certificate chain for the certificate with the given UUID. " +
                    "The certificate chain is returned in the order of the chain, with the first certificate " +
                    "being the certificate with the given UUID, up to the last identified certificate in the chain. " +
                    "If the certificate with the given UUID has status `NEW` or `REJECTED`, an empty list is returned."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Certificate chain retrieved")
    })
    @GetMapping(
            path = "/{uuid}/chain",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    CertificateChainResponseDto getCertificateChain(@Parameter(description = "Certificate UUID") @PathVariable UUID uuid, @RequestParam(required = false) boolean withEndCertificate) throws NotFoundException;

    @Operation(summary = "Download Certificate Chain in chosen format")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Chain certificates downloaded")})
    @GetMapping(
            path = {"/{uuid}/chain/{certificateFormat}"},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    CertificateChainDownloadResponseDto downloadCertificateChain(@Parameter(description = "Certificate UUID") @PathVariable UUID uuid, @Parameter(description = "Certificate format") @PathVariable CertificateFormat certificateFormat, @RequestParam(required = false) boolean withEndCertificate, @RequestParam CertificateFormatEncoding encoding) throws NotFoundException, CertificateException;

    @Operation(summary = "List Certificates Approvals")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of all approvals for the certificate")})
    @GetMapping(path = "/{uuid}/approvals", produces = {MediaType.APPLICATION_JSON_VALUE})
    ApprovalResponseDto listCertificateApprovals(@Parameter(description = "Certificate UUID") @PathVariable UUID uuid, final PaginationRequestDto paginationRequestDto);

    @Operation(summary = "Archive a certificate")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Certificate archived")})
    @PatchMapping(path = "/{uuid}/archive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void archiveCertificate(@Parameter(description = "Certificate UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(summary = "Unarchive a certificate")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Certificate unarchived")})
    @PatchMapping(path = "/{uuid}/unarchive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void unarchiveCertificate(@Parameter(description = "Certificate UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(summary = "Archive a list of certificates")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Certificates archived")})
    @PatchMapping(path = "/archive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void bulkArchiveCertificate(@RequestBody List<UUID> uuids);

    @Operation(summary = "Unarchive a list of certificates")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Certificates unarchived")})
    @PatchMapping(path = "/unarchive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void bulkUnarchiveCertificate(@RequestBody List<UUID> uuids);

    @Operation(summary = "Get relations for a certificate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Certificate relations retrieved successfully")
    })
    @GetMapping("/{uuid}/relations")
    CertificateRelationsDto getCertificateRelations(@PathVariable UUID uuid) throws NotFoundException;

    @Operation(summary = "Associate a source certificate to the given certificate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Certificates associated successfully")
    })
    @PatchMapping("/{uuid}/relations/{certificateUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void associateCertificates(@PathVariable @Parameter(description = "Certificate UUID") UUID uuid, @PathVariable @Parameter(description = "UUID of certificate to associate the certificate with") UUID certificateUuid) throws NotFoundException;

    @Operation(summary = "Remove a source certificate association from the given certificate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Certificate association removed successfully")
    })
    @DeleteMapping("/{uuid}/relations/{certificateUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeCertificateAssociation(@PathVariable @Parameter(description = "Certificate UUID") UUID uuid, @PathVariable @Parameter(description = "UUID of certificate to disassociate the certificate with") UUID certificateUuid) throws NotFoundException;

}
