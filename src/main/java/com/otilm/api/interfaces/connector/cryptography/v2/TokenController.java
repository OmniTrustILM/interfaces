package com.otilm.api.interfaces.connector.cryptography.v2;

import com.otilm.api.interfaces.connector.common.v2.AuthProtectedConnectorController;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.error.ProblemDetailExtended;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenStatusResponseV2Dto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Connector-facing V2 token-management interface.
 *
 * <p>
 * Token requests are scoped by token and token-profile attributes supplied in the request body.
 * </p>
 */
@RequestMapping("/v2/cryptographyProvider/tokens")
@Tag(name = "Token Management v2",
        description = "Token configuration, status and token-profile schema operations for cryptography providers")
public interface TokenController extends AuthProtectedConnectorController {

    @Operation(summary = "List token attributes",
            description = "Returns the attribute schema used to configure a token")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Token attributes retrieved"))
    @GetMapping(path = "/attributes", produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listTokenAttributes();

    @Operation(summary = "Check token status",
            description = "Evaluates the supplied token context and returns token reachability, readiness, "
                    + "and lockout information when available")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token status retrieved"),
            @ApiResponse(responseCode = "400",
                    description = "Request body cannot be read (errorCode BAD_REQUEST), including malformed JSON, "
                            + "unknown properties, and values outside the published enum values",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = "Request body was read successfully but violates a field validation rule "
                            + "(errorCode VALIDATION_FAILED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/status", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    TokenStatusResponseV2Dto getTokenStatus(@RequestBody @Valid TokenScopedRequestV2Dto request);

    @Operation(summary = "List token profile attributes",
            description = "Returns the token profile attribute schema for the supplied token context")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token profile attributes retrieved"),
            @ApiResponse(responseCode = "400",
                    description = "Request body cannot be read (errorCode BAD_REQUEST), including malformed JSON, "
                            + "unknown properties, and values outside the published enum values",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = "Request body was read successfully but violates a field validation rule "
                            + "(errorCode VALIDATION_FAILED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/tokenProfile/attributes", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listTokenProfileAttributes(@RequestBody @Valid TokenScopedRequestV2Dto request);

    @Operation(summary = "List key request types supported by the Token Profile",
            description = "List key types supported for the supplied token and token-profile context")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supported key types retrieved"),
            @ApiResponse(responseCode = "400",
                    description = "Request body cannot be read (errorCode BAD_REQUEST), including malformed JSON, "
                            + "unknown properties, and values outside the published enum values",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = "Request body was read successfully but violates a field validation rule "
                            + "(errorCode VALIDATION_FAILED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})

    @PostMapping(path = "/keyRequestTypes", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<@NotNull KeyRequestType> listSupportedKeyRequestTypes(
            @RequestBody @Valid TokenProfileScopedRequestV2Dto request);
}
