package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.trustedcertificate.TrustedCertificateAddRequestDto;
import com.otilm.api.model.client.trustedcertificate.TrustedCertificateApplyRequestDto;
import com.otilm.api.model.client.trustedcertificate.TrustedCertificateBulkResultDto;
import com.otilm.api.model.client.trustedcertificate.TrustedCertificateDto;
import com.otilm.api.model.client.trustedcertificate.TrustedCertificatePlatformDto;
import com.otilm.api.model.client.trustedcertificate.TrustedCertificateRemoveRequestDto;
import com.otilm.api.model.client.trustedcertificate.TrustedCertificateUpdateRequestDto;
import com.otilm.api.model.common.ErrorMessageDto;
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
import java.security.cert.CertificateException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/v1/trustedCertificates")
@Tag(name = "Trusted Certificate Management", description = "Trusted Certificate Management API")
public interface TrustedCertificateController extends AuthProtectedController {

    String BULK_RESULT_DESCRIPTION = "One result per distinct certificate; item failures never change the status code";
    String UNPROCESSABLE_CONTENT = "Unprocessable Content";
    String UNPROCESSABLE_CONTENT_EXAMPLE = "[\"Error Message 1\",\"Error Message 2\"]";

    @Operation(summary = "List Trusted Certificates")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of Trusted Certificates")})
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    List<TrustedCertificateDto> listTrustedCertificates();

    @Operation(summary = "Get details of a Trusted Certificate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trusted Certificate details retrieved"),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    TrustedCertificateDto getTrustedCertificate(
            @Parameter(description = "Trusted Certificate UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Add Trusted Certificates",
            description = "Trusts several inventory certificates or one uploaded certificate for the given purposes. "
                    + "A certificate that cannot be trusted fails only its own result.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = BULK_RESULT_DESCRIPTION),
            @ApiResponse(responseCode = "422", description = UNPROCESSABLE_CONTENT,
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = UNPROCESSABLE_CONTENT_EXAMPLE)}))})
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    TrustedCertificateBulkResultDto addTrustedCertificates(@RequestBody @Valid TrustedCertificateAddRequestDto request)
            throws CertificateException;

    @Operation(summary = "Update purposes of Trusted Certificates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = BULK_RESULT_DESCRIPTION),
            @ApiResponse(responseCode = "422", description = UNPROCESSABLE_CONTENT,
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = UNPROCESSABLE_CONTENT_EXAMPLE)}))})
    @PatchMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    TrustedCertificateBulkResultDto updateTrustedCertificates(
            @RequestBody @Valid TrustedCertificateUpdateRequestDto request);

    @Operation(summary = "Remove trust from Trusted Certificates",
            description = "The certificates stay in the inventory.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = BULK_RESULT_DESCRIPTION),
            @ApiResponse(responseCode = "422", description = UNPROCESSABLE_CONTENT,
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = UNPROCESSABLE_CONTENT_EXAMPLE)}))})
    @DeleteMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    TrustedCertificateBulkResultDto removeTrustedCertificates(
            @RequestBody @Valid TrustedCertificateRemoveRequestDto request);

    @Operation(summary = "Delete a Trusted Certificate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Trusted Certificate deleted"),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    void deleteTrustedCertificate(@Parameter(description = "Trusted Certificate UUID") @PathVariable String uuid)
            throws NotFoundException;

    /**
     * Runs the platform comparison, then re-sends rows that are pending, failed or missing and retries failed removals;
     * never re-creates a row whose removal is pending.
     */
    @Operation(summary = "Re-apply Trusted Certificates on the platform",
            description = "Compares the trusted certificates with the platform, then applies again the ones that are "
                    + "pending, failed or missing on the platform, and retries failed removals. A certificate whose "
                    + "removal is pending stays removed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = BULK_RESULT_DESCRIPTION),
            @ApiResponse(responseCode = "422", description = UNPROCESSABLE_CONTENT,
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = UNPROCESSABLE_CONTENT_EXAMPLE)}))})
    @PostMapping(path = "/apply", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    TrustedCertificateBulkResultDto applyTrustedCertificates(@RequestBody TrustedCertificateApplyRequestDto request);

    @Operation(summary = "Get platform trust",
            description = "Whether ILM manages platform trust in this deployment and, when the deployment bundle is "
                    + "the source, the certificates it holds.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Platform trust retrieved")})
    @GetMapping(path = "/platform", produces = {MediaType.APPLICATION_JSON_VALUE})
    TrustedCertificatePlatformDto getPlatformTrust();

}
