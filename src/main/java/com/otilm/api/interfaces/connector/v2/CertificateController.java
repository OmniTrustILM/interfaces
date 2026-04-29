package com.otilm.api.interfaces.connector.v2;

import com.otilm.api.exception.CertificateOperationException;
import com.otilm.api.exception.CertificateRequestException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.interfaces.AuthProtectedConnectorController;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.v2.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
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
            summary = "Issue Certificate"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Certificate issued"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @PostMapping(path = "/issue", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    CertificateDataResponseDto issueCertificate(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid,
            @RequestBody CertificateSignRequestDto request) throws NotFoundException, CertificateOperationException, CertificateRequestException;

    @Operation(
            summary = "Renew Certificate"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Certificate renewed"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @PostMapping(path = "/renew", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    CertificateDataResponseDto renewCertificate(
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
            summary = "Revoke Certificate"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Certificate revoked"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @PostMapping(path = "/revoke", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    void revokeCertificate(
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
}
