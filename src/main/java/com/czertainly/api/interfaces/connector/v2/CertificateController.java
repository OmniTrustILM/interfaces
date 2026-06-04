package com.czertainly.api.interfaces.connector.v2;

import com.czertainly.api.exception.CertificateOperationException;
import com.czertainly.api.exception.CertificateRequestException;
import com.czertainly.api.exception.NotFoundException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.interfaces.AuthProtectedConnectorController;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.v2.*;
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

import java.util.List;

@RequestMapping("/v2/authorityProvider/authorities/{uuid}/certificates")
@Tag(name = "Certificate Management", description = "Certificate Management API")
public interface CertificateController extends AuthProtectedConnectorController {

    @Operation(
            summary = "List of Attributes to issue Certificate"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Issue certificate attribute list retrieved"
                    )
            })
    @GetMapping(path = "/issue/attributes", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BaseAttribute> listIssueCertificateAttributes(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(
            summary = "Validate list of Attributes to issue Certificate"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Attributes validated"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @PostMapping(path = "/issue/attributes/validate", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    void validateIssueCertificateAttributes(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid,
            @RequestBody List<RequestAttribute>attributes) throws NotFoundException, ValidationException;

    @Operation(
            summary = "Issue Certificate",
            description = "Issue a certificate. The Authority Provider may complete the operation "
                    + "synchronously or asynchronously."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Certificate issued synchronously; response carries the issued certificate."
                    ),
                    @ApiResponse(
                            responseCode = "202",
                            description = "Issuance is asynchronous; the operation will complete asynchronously. The "
                                    + "optional response body may carry `MetadataAttribute` entries — technology-"
                                    + "specific state — that the platform will return on subsequent calls related "
                                    + "to this operation.",
                            content = @Content(schema = @Schema(implementation = CertificateDataResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @PostMapping(path = "/issue", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<CertificateDataResponseDto> issueCertificate(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid,
            @RequestBody CertificateSignRequestDto request) throws NotFoundException, CertificateOperationException, CertificateRequestException;

    @Operation(
            summary = "Renew Certificate",
            description = "Renew a certificate. The Authority Provider may complete the operation "
                    + "synchronously or asynchronously."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Certificate renewed synchronously; response carries the new certificate."
                    ),
                    @ApiResponse(
                            responseCode = "202",
                            description = "Renewal is asynchronous; the operation will complete asynchronously. The "
                                    + "optional response body may carry `MetadataAttribute` entries — technology-"
                                    + "specific state — that the platform will return on subsequent calls related "
                                    + "to this operation.",
                            content = @Content(schema = @Schema(implementation = CertificateDataResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @PostMapping(path = "/renew", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<CertificateDataResponseDto> renewCertificate(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid,
            @RequestBody CertificateRenewRequestDto request) throws NotFoundException, CertificateOperationException, CertificateRequestException;

    @Operation(
            summary = "List of Attributes to revoke Certificate"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Revoke certificate attribute list retrieved"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @GetMapping(path = "/revoke/attributes", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BaseAttribute> listRevokeCertificateAttributes(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(
            summary = "Validate list of Attributes to revoke certificate"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Attributes validated"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @PostMapping(path = "/revoke/attributes/validate", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    void validateRevokeCertificateAttributes(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid,
            @RequestBody List<RequestAttribute>attributes) throws NotFoundException, ValidationException;

    @Operation(
            summary = "Revoke Certificate",
            description = "Revoke a certificate. The Authority Provider may complete the operation "
                    + "synchronously or asynchronously."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Certificate revoked synchronously."
                    ),
                    @ApiResponse(
                            responseCode = "202",
                            description = "Revocation is asynchronous; the operation will complete asynchronously. The "
                                    + "response body is empty for revocation — the platform tracks the operation by "
                                    + "transactionId / certificate identity, not by connector-emitted metadata."
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @PostMapping(path = "/revoke", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<Void> revokeCertificate(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid,
            @RequestBody CertRevocationDto request) throws NotFoundException, CertificateOperationException;

    @Operation(
            summary = "Identify Certificate"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Certificate identified"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Certificate unknown",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}
                            )),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity. Certificate is found but not valid according to supplied RA attributes",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @PostMapping(path = "/identify", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    CertificateIdentificationResponseDto identifyCertificate(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid,
            @RequestBody CertificateIdentificationRequestDto request) throws NotFoundException, ValidationException;

    @Operation(
            summary = "Cancel Certificate Issuance or Renewal",
            description = """
                    Abort an in-flight certificate issuance or renewal.

                    Called for an asynchronous issuance, renewal, or rekey that previously
                    responded `202 Accepted`. The request body carries the RA profile
                    attributes and the same `MetadataAttribute` entries returned in the
                    original `202 Accepted` response, so the Authority Provider can resolve
                    the operation it must abort.
                    """
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "The operation has been aborted."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "The Authority Provider does not track this operation (e.g., the operation has "
                                    + "already completed and was forgotten, or the implementation is stateless)."
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "The Authority Provider tracks the operation but refuses to abort it (e.g., the "
                                    + "underlying CA does not support cancel, or the issuance is past a point of no "
                                    + "return).",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"CA does not support cancellation\",\"Issuance is past the point of no return\"]")}
                            ))
            })
    @PostMapping(path = "/issue/cancel", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void cancelIssueCertificate(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid,
            @RequestBody CertificateOperationCancelRequestDto request) throws NotFoundException, ValidationException;

    @Operation(
            summary = "Cancel Certificate Revocation",
            description = """
                    Abort an in-flight certificate revocation.

                    Called for an asynchronous revocation that previously responded
                    `202 Accepted`. The request body carries the RA profile attributes and the
                    same `MetadataAttribute` entries returned in the original `202 Accepted`
                    response.
                    """
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "The operation has been aborted."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "The Authority Provider does not track this operation."
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "The Authority Provider tracks the operation but refuses to abort it (e.g., the "
                                    + "revocation has already been submitted to a CRL).",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"CA does not support cancellation\",\"Revocation already submitted to CRL\"]")}
                            ))
            })
    @PostMapping(path = "/revoke/cancel", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void cancelRevokeCertificate(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid,
            @RequestBody CertificateOperationCancelRequestDto request) throws NotFoundException, ValidationException;

    @Operation(
            summary = "Get Certificate Issuance Status",
            description = """
                    Return the current status of an asynchronous certificate issuance.

                    Called for an asynchronous issuance or renewal that previously responded
                    `202 Accepted`. The request body carries the RA profile attributes and the
                    same `MetadataAttribute` entries returned in the original `202 Accepted`
                    response, so the Authority Provider can resolve the operation.
                    """
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Status retrieved. Response carries one of `IN_PROGRESS`, `COMPLETED`, or "
                                    + "`FAILED`. When `COMPLETED`, the response includes the issued certificate "
                                    + "(Base64) in `certificateData`."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "The Authority Provider does not track this operation."
                    )
            })
    @PostMapping(path = "/issue/status", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    CertificateOperationStatusResponseDto getIssueCertificateStatus(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid,
            @RequestBody CertificateOperationStatusRequestDto request) throws NotFoundException;

    @Operation(
            summary = "Get Certificate Revocation Status",
            description = """
                    Return the current status of an asynchronous certificate revocation.

                    Called for an asynchronous revocation that previously responded
                    `202 Accepted`. For revocation, the `certificateData` field of the response
                    is unused (revocation completion carries no payload).
                    """
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Status retrieved. Response carries one of `IN_PROGRESS`, `COMPLETED`, or "
                                    + "`FAILED`."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "The Authority Provider does not track this operation."
                    )
            })
    @PostMapping(path = "/revoke/status", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    CertificateOperationStatusResponseDto getRevokeCertificateStatus(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid,
            @RequestBody CertificateOperationStatusRequestDto request) throws NotFoundException;
}
