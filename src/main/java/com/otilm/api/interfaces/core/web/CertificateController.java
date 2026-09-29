package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.AttributeException;
import com.otilm.api.exception.CertificateOperationException;
import com.otilm.api.exception.CertificateRequestException;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.exception.NotSupportedException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.interfaces.core.web.v2.ComplianceController;
import com.otilm.api.model.client.approval.ApprovalResponseDto;
import com.otilm.api.model.client.certificate.BulkOperationResponse;
import com.otilm.api.model.client.certificate.CertificateComplianceCheckDto;
import com.otilm.api.model.client.certificate.CertificateImportRequestDto;
import com.otilm.api.model.client.certificate.CertificateImportResponseDto;
import com.otilm.api.model.client.certificate.CertificateKeystoreRequestDto;
import com.otilm.api.model.client.certificate.CertificateResponseDto;
import com.otilm.api.model.client.certificate.CertificateSearchRequestDto;
import com.otilm.api.model.client.certificate.CertificateUpdateObjectsDto;
import com.otilm.api.model.client.certificate.MultipleCertificateObjectUpdateDto;
import com.otilm.api.model.client.certificate.RemoveCertificateDto;
import com.otilm.api.model.client.certificate.UploadCertificateRequestDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.common.UuidDto;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.core.certificate.CertificateChainDownloadResponseDto;
import com.otilm.api.model.core.certificate.CertificateChainResponseDto;
import com.otilm.api.model.core.certificate.CertificateContentDto;
import com.otilm.api.model.core.certificate.CertificateDetailDto;
import com.otilm.api.model.core.certificate.CertificateDownloadResponseDto;
import com.otilm.api.model.core.certificate.CertificateEventHistoryDto;
import com.otilm.api.model.core.certificate.CertificateFormat;
import com.otilm.api.model.core.certificate.CertificateFormatEncoding;
import com.otilm.api.model.core.certificate.CertificateRelationsDto;
import com.otilm.api.model.core.certificate.CertificateValidationResultDto;
import com.otilm.api.model.core.certificate.FingerprintDto;
import com.otilm.api.model.core.location.LocationDto;
import com.otilm.api.model.core.scheduler.PaginationRequestDto;
import com.otilm.api.model.core.search.ConfigurableColumnsDocs;
import com.otilm.api.model.core.search.SearchFieldDataByGroupDto;
import com.otilm.api.model.core.v2.ClientCertificateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

@RequestMapping("/v1/certificates")
@Tag(name = "Certificate Inventory", description = "Certificate Inventory API")
@ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Not Found",
                content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
public interface CertificateController extends AuthProtectedController {

    /** Media type of a PKCS#12 keystore download. */
    String KEYSTORE_MEDIA_TYPE = "application/x-pkcs12";

    @Operation(summary = "List Certificates",
            description = ConfigurableColumnsDocs.SORT_AND_COLUMNS + ConfigurableColumnsDocs.ATTRIBUTE_PROJECTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all the certificates"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    CertificateResponseDto listCertificates(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(implementation = CertificateSearchRequestDto.class),
                    examples = {@ExampleObject(name = "With ordering and columns", value = """
                            {
                              "pageNumber": 1,
                              "itemsPerPage": 10,
                              "filters": [],
                              "sort": {"fieldSource": "property", "fieldIdentifier": "NOT_AFTER", "direction": "asc"},
                              "columns": [
                                {"fieldSource": "property", "fieldIdentifier": "COMMON_NAME"},
                                {"fieldSource": "property", "fieldIdentifier": "NOT_AFTER"},
                                {"fieldSource": "custom", "fieldIdentifier": "businessUnit|STRING"}
                              ]
                            }""")})) @Valid @RequestBody CertificateSearchRequestDto request);

    @Operation(summary = "Get Certificate Details")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Certificate detail retrieved")})
    @GetMapping(path = "/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    CertificateDetailDto getCertificate(@Parameter(description = "Certificate UUID") @PathVariable UUID uuid)
            throws NotFoundException, CertificateException, IOException;

    @Operation(summary = "Download Certificate")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Certificate downloaded")})
    @GetMapping(path = "/{uuid}/{certificateFormat}", produces = {MediaType.APPLICATION_JSON_VALUE})
    CertificateDownloadResponseDto downloadCertificate(
            @Parameter(description = "Certificate UUID") @PathVariable UUID uuid,
            @Parameter(description = "Certificate format") @PathVariable CertificateFormat certificateFormat,
            @RequestParam CertificateFormatEncoding encoding)
            throws NotFoundException, CertificateException, IOException;

    @Operation(summary = "Delete a certificate")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Certificate deleted")})
    @DeleteMapping(path = "/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCertificate(@Parameter(description = "Certificate UUID") @PathVariable UUID uuid)
            throws NotFoundException;

    @Operation(summary = "Update Certificate Objects")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Certificate objects updated")})
    @PatchMapping(path = "/{uuid}", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateCertificateObjects(@Parameter(description = "Certificate UUID") @PathVariable UUID uuid,
            @RequestBody CertificateUpdateObjectsDto request)
            throws NotFoundException, CertificateOperationException, ValidationException, AttributeException;

    @Operation(summary = "Update Group and/or Owner for multiple Certificates",
            description = "In this operation, when the list of "
                    + "Certificate UUIDs are provided and the filter is left as null or undefined, then the change will "
                    + "be applied only to the list of Certificate UUIDs provided. When the filter is provided in the request, "
                    + "the list of UUIDs will be ignored and the change will be applied for the all the certificates that matches "
                    + "the filter criteria. To apply this change for all the Certificates in the inventory, "
                    + "provide an empty array \"[]\" for the value of \"filters\" in the request body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Certificate objects updated"),
            @ApiResponse(responseCode = "501", description = "Certificate objects update by filters not supported")})
    @PatchMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void bulkUpdateCertificateObjects(@RequestBody MultipleCertificateObjectUpdateDto request)
            throws NotFoundException, NotSupportedException;

    @Operation(summary = "Upload a new Certificate")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "202", description = "Certificate accepted for processing of upload")})
    @PostMapping(path = "/upload/async", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.ACCEPTED)
    FingerprintDto uploadAsync(@RequestBody UploadCertificateRequestDto request)
            throws AlreadyExistException, CertificateException;

    @Operation(summary = "Upload a new Certificate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Certificate uploaded",
                    content = @Content(schema = @Schema(implementation = UuidDto.class)))})
    @PostMapping(path = "/upload", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<UuidDto> upload(@RequestBody UploadCertificateRequestDto request) throws AlreadyExistException,
            CertificateException, NoSuchAlgorithmException, NotFoundException, AttributeException;

    @Operation(summary = "Import certificates and keys from an uploaded file", description = """
            Import named entries from an uploaded file.

            The file travels base64-encoded in `file`, like every upload in the platform, and is held in memory rather
            than written anywhere; it is never echoed in an error, since it may carry key material. Every entry to
            import is named, and nothing else in the file is touched: an import never takes in material the caller did
            not ask for.

            An entry reference is derived from the entry's own content — a certificate fingerprint, a public key
            fingerprint, or a digest of the protected key — so naming one selects that content, not a position in a
            file. A caller showing a user what is inside reads the file first with `POST /v1/inspections` and names
            what the user chose. A caller that already knows what it is importing computes the reference itself and
            imports in one call.

            Each entry says where its own key material goes, because a file can hold entries of different key types
            and each key type has its own provider attribute schema.

            Entries succeed or fail on their own and the response reports the outcome of each. Each entry carries its
            own `importId`, so repeating a request returns what already succeeded and retries only what did not: a
            caller recovering from a lost or partial response resends the same body and needs to work out nothing.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Selected entries imported"),
            @ApiResponse(responseCode = "404", description = "Token profile not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "409",
                    description = "an entry's `importId` was already used to import something else",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422",
                    description = "the file cannot be read, or the selection breaks a rule: an entry the file does "
                            + "not hold, a certificate request, key material with nowhere to go or a destination for "
                            + "an entry without key material, or a key with no name to take",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(path = "/import", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    CertificateImportResponseDto importCertificates(@RequestBody @Valid CertificateImportRequestDto request)
            throws ValidationException, NotFoundException, ConnectorException, AttributeException, CertificateException,
            IOException;

    @Operation(summary = "Download a certificate with its private key", description = """
            Download a certificate, its chain and its private key as one PKCS#12 file.

            This is a POST because the passphrase travels in the body: a URL is recorded by proxies, browser history
            and access logs, so a passphrase must never appear in one. The response is not cacheable and carries a
            sanitized download filename.

            It is a separate operation from the certificate content download, which serves certificates only. Only a
            key created or imported as exportable can be included.

            keytool opens the file only when its passphrase is printable ASCII.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Keystore downloaded",
                    headers = {
                            @Header(name = HttpHeaders.CONTENT_DISPOSITION, required = true,
                                    description = "Attachment with a sanitized filename, as RFC 6266 describes",
                                    schema = @Schema(type = "string")),
                            @Header(name = HttpHeaders.CACHE_CONTROL, required = true,
                                    description = "`no-store, no-cache`, so the file is not written to any cache",
                                    schema = @Schema(type = "string")),
                            @Header(name = HttpHeaders.PRAGMA, required = true,
                                    description = "`no-cache`, for caches that predate `Cache-Control`",
                                    schema = @Schema(type = "string")),
                            @Header(name = "X-Content-Type-Options", required = true, description = "`nosniff`",
                                    schema = @Schema(type = "string"))},
                    content = @Content(mediaType = KEYSTORE_MEDIA_TYPE,
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "Certificate or its key not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(path = "/{uuid}/keystore", consumes = MediaType.APPLICATION_JSON_VALUE, produces = KEYSTORE_MEDIA_TYPE)
    ResponseEntity<Resource> downloadKeystore(@Parameter(description = "Certificate UUID") @PathVariable UUID uuid,
            @RequestBody @Valid CertificateKeystoreRequestDto request) throws NotFoundException, ValidationException,
            ConnectorException, AttributeException, CertificateException, IOException;

    @Operation(summary = "Delete multiple certificates", description = "In this operation, when the list of "
            + "Certificate UUIDs are provided and the filter is left as null or undefined, then the change will "
            + "be applied only to the list of Certificate UUIDs provided. When the filter is provided in the request, "
            + "the list of UUIDs will be ignored and the change will be applied for the all the certificates that matches "
            + "the filter criteria. To apply this change for all the Certificates in the inventory, "
            + "provide an empty array \"[]\" for the value of \"filters\" in the request body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Certificates deleted"),
            @ApiResponse(responseCode = "501", description = "Certificate objects delete by filters not supported")})
    @PostMapping(path = "/delete", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    BulkOperationResponse bulkDeleteCertificate(@RequestBody RemoveCertificateDto request)
            throws NotFoundException, NotSupportedException;

    @Operation(operationId = "getCertificateSearchableFields",
            summary = "Get Certificate searchable fields information",
            description = ConfigurableColumnsDocs.CATALOGUE_FLAGS)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Certificate searchable field information retrieved")})
    @GetMapping(path = "/search", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<SearchFieldDataByGroupDto> getSearchableFieldInformation();

    @Operation(summary = "Get Certificate event history")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Certificate event history retrieved")})
    @GetMapping(path = "/{uuid}/history", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<CertificateEventHistoryDto> getCertificateEventHistory(
            @Parameter(description = "Certificate UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(summary = "List of available Locations for the Certificate", operationId = "listCertificateLocations")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Locations retrieved")})
    @GetMapping(path = "/{certificateUuid}/locations", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<LocationDto> listLocations(@Parameter(description = "Certificate UUID") @PathVariable UUID certificateUuid)
            throws NotFoundException;

    /**
     * @deprecated As of release 2.16.0. Replaced by {@link ComplianceController#checkResourceObjectsCompliance} with
     * resource Certificate.
     */
    @Deprecated(since = "2.16.0", forRemoval = true)
    @Operation(summary = "Initiate Certificate Compliance Check", operationId = "checkCertificatesCompliance",
            deprecated = true)
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Compliance check initiated")})
    @PostMapping(path = "/compliance", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void checkCompliance(@Valid @RequestBody CertificateComplianceCheckDto request) throws NotFoundException;

    @Operation(summary = "Get Certificate Validation Result")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Certificate validation detail retrieved")})
    @GetMapping(path = "/{uuid}/validate", produces = {MediaType.APPLICATION_JSON_VALUE})
    CertificateValidationResultDto getCertificateValidationResult(
            @Parameter(description = "Certificate UUID") @PathVariable UUID uuid)
            throws NotFoundException, CertificateException;

    @Operation(summary = "Get CSR Generation Attributes", description = """
            Returns the request-attribute definitions available for building a certificate request.
            Without `raProfileUuid`: the editable platform default request-attribute set.
            With `raProfileUuid`: the resolved request-attribute set for that RA profile.""")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),

            @ApiResponse(responseCode = "200", description = "CSR Generation attributes retrieved"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/csr/attributes", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BaseAttribute> getCsrGenerationAttributes(@Parameter(
            description = "RA Profile UUID — when provided, the response is the resolved request-attribute set for this RA profile") @RequestParam(
                    required = false) UUID raProfileUuid)
            throws NotFoundException, ConnectorException;

    @Operation(summary = "Get Certificate Content")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Certificate content retrieved"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(path = "/content", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    List<CertificateContentDto> getCertificateContent(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Certificate UUIDs",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {
                    @ExampleObject(
                            value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<UUID> uuids);

    @Operation(summary = "Submit certificate request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),

            @ApiResponse(responseCode = "200",
                    description = "Certificate request submit, certificate created and ready to be issued")})
    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    CertificateDetailDto submitCertificateRequest(@RequestBody ClientCertificateRequestDto request)
            throws ValidationException, ConnectorException, CertificateException, IOException, NoSuchAlgorithmException,
            InvalidKeyException, NoSuchProviderException, AttributeException, CertificateRequestException,
            NotFoundException;

    @Operation(summary = "Get certificate chain",
            description = "Get certificate chain for the certificate with the given UUID. "
                    + "The certificate chain is returned in the order of the chain, with the first certificate "
                    + "being the certificate with the given UUID, up to the last identified certificate in the chain. "
                    + "If the certificate with the given UUID has status `NEW` or `REJECTED`, an empty list is returned.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Certificate chain retrieved")})
    @GetMapping(path = "/{uuid}/chain", produces = MediaType.APPLICATION_JSON_VALUE)
    CertificateChainResponseDto getCertificateChain(
            @Parameter(description = "Certificate UUID") @PathVariable UUID uuid,
            @RequestParam(required = false) boolean withEndCertificate) throws NotFoundException;

    @Operation(summary = "Download Certificate Chain in chosen format")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Chain certificates downloaded")})
    @GetMapping(path = {"/{uuid}/chain/{certificateFormat}"}, produces = {MediaType.APPLICATION_JSON_VALUE})
    CertificateChainDownloadResponseDto downloadCertificateChain(
            @Parameter(description = "Certificate UUID") @PathVariable UUID uuid,
            @Parameter(description = "Certificate format") @PathVariable CertificateFormat certificateFormat,
            @RequestParam(required = false) boolean withEndCertificate,
            @RequestParam CertificateFormatEncoding encoding) throws NotFoundException, CertificateException;

    @Operation(summary = "List Certificates Approvals")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "200", description = "List of all approvals for the certificate")})
    @GetMapping(path = "/{uuid}/approvals", produces = {MediaType.APPLICATION_JSON_VALUE})
    ApprovalResponseDto listCertificateApprovals(@Parameter(description = "Certificate UUID") @PathVariable UUID uuid,
            final PaginationRequestDto paginationRequestDto);

    @Operation(summary = "Archive a certificate")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Certificate archived")})
    @PatchMapping(path = "/{uuid}/archive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void archiveCertificate(@Parameter(description = "Certificate UUID") @PathVariable UUID uuid)
            throws NotFoundException;

    @Operation(summary = "Unarchive a certificate")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Certificate unarchived")})
    @PatchMapping(path = "/{uuid}/unarchive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void unarchiveCertificate(@Parameter(description = "Certificate UUID") @PathVariable UUID uuid)
            throws NotFoundException;

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
    @ApiResponses(
            value = {@ApiResponse(responseCode = "200", description = "Certificate relations retrieved successfully")})
    @GetMapping("/{uuid}/relations")
    CertificateRelationsDto getCertificateRelations(@PathVariable UUID uuid) throws NotFoundException;

    @Operation(summary = "Associate a source certificate to the given certificate")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Certificates associated successfully")})
    @PatchMapping("/{uuid}/relations/{certificateUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void associateCertificates(@PathVariable @Parameter(description = "Certificate UUID") UUID uuid,
            @PathVariable @Parameter(
                    description = "UUID of certificate to associate the certificate with") UUID certificateUuid)
            throws NotFoundException;

    @Operation(summary = "Remove a source certificate association from the given certificate")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "204", description = "Certificate association removed successfully")})
    @DeleteMapping("/{uuid}/relations/{certificateUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeCertificateAssociation(@PathVariable @Parameter(description = "Certificate UUID") UUID uuid,
            @PathVariable @Parameter(
                    description = "UUID of certificate to disassociate the certificate with") UUID certificateUuid)
            throws NotFoundException;

}
