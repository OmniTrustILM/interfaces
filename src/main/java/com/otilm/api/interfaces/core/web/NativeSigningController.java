package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.NotFoundException;
import com.otilm.api.exception.SigningException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.signing.nativesigning.SignDocumentRequestDto;
import com.otilm.api.model.client.signing.nativesigning.SignDocumentResponseDto;
import com.otilm.api.model.common.ErrorMessageDto;
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
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ILM's own signing API for server-side integrations. Every operation authorizes as {@code ResourceAction.SIGN} on
 * {@code Resource.SIGNING_PROFILE}. Operations are synchronous and carry no signature-activation evidence. Each
 * operation documents its own 404 and 422.
 */
@RequestMapping("/v1/signing")
@Tag(name = "Native Signing",
        description = "Native Signing API. "
                + "Synchronous signing operations for server-side integrations, authorized against the Signing Profile "
                + "that is used. The Signing Profile must have the ILM Native API activated; otherwise the operation "
                + "answers 422.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "502", description = "A connector or the signer failed",
                content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
        @ApiResponse(responseCode = "503",
                description = "A connector or the time source is unavailable; retry after the Retry-After interval",
                headers = @Header(name = "Retry-After", description = "Seconds after which the request may be retried",
                        schema = @Schema(type = "integer")),
                content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
public interface NativeSigningController extends AuthProtectedController {

    @Operation(operationId = "signDocument", summary = "Sign documents",
            description = "Sign every document of the request at the Signing Profile's signature level and return "
                    + "the signing outputs in request order. The request succeeds or fails as a whole: a failed request "
                    + "returns no output and writes no signing record for its documents. A retry is a new signing. A signing "
                    + "failure names the item by its identifier; a validation failure names the field path by item "
                    + "index.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documents signed"),
            @ApiResponse(responseCode = "404", description = "Signing Profile not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422",
                    description = "Request cannot be processed: the Signing Profile is disabled, has the ILM Native API "
                            + "inactive, does not sign content or does not accept the transfer mode, the request body "
                            + "violates a field validation rule, the request carries more items than the platform's "
                            + "items-per-request cap, `digestAlgorithm` differs from the digest the Signing Profile's "
                            + "signature algorithm commits to, the Signing Profile refuses a request attribute, or the "
                            + "formatting connector refuses a document",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(path = "/signingProfiles/{signingProfileUuid}/documents/sign",
            consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    SignDocumentResponseDto signDocument(
            @Parameter(description = "Signing Profile UUID") @PathVariable UUID signingProfileUuid,
            @RequestBody @Valid SignDocumentRequestDto request) throws NotFoundException, SigningException;
}
