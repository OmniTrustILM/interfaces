package com.otilm.api.interfaces.connector.cryptography.v2;

import com.otilm.api.interfaces.connector.common.v2.AuthProtectedConnectorController;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.v2.token.TokenScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenStatusResponseV2Dto;
import com.otilm.api.model.core.cryptography.key.KeyUsage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Connector-facing V2 token-management interface.
 *
 * <p>Token requests are scoped by token and token-profile attributes supplied in the request body.</p>
 */
@RequestMapping("/v2/cryptographyProvider/tokens")
@Tag(name = "Token Management v2",
        description = "Token configuration, status and token-profile schema operations for cryptography providers")
public interface TokenController extends AuthProtectedConnectorController {

    @Operation(summary = "List token attributes", description = "Returns the attribute schema used to configure a token")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Token attributes retrieved"))
    @GetMapping(path = "/attributes", produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listTokenAttributes();

    @Operation(summary = "Check token status",
            description = "Evaluates the supplied token context and returns token reachability, readiness, "
                    + "component status and lockout information when available")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Token status retrieved"))
    @PostMapping(path = "/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    TokenStatusResponseV2Dto getTokenStatus(@RequestBody @Valid TokenScopedRequestV2Dto request);

    @Operation(summary = "List token profile attributes",
            description = "Returns the token profile attribute schema for the supplied token context")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Token profile attributes retrieved"))
    @PostMapping(path = "/tokenProfile/attributes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listTokenProfileAttributes(@RequestBody @Valid TokenScopedRequestV2Dto request);

    @Operation(summary = "List supported token profile key usages",
            description = "Returns the key usages supported for the supplied token context")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Supported key usages retrieved"))
    @PostMapping(path = "/tokenProfile/keyUsages", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<KeyUsage> listTokenProfileKeyUsages(@RequestBody @Valid TokenScopedRequestV2Dto request);
}
