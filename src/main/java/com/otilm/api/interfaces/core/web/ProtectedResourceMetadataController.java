package com.otilm.api.interfaces.core.web;

import com.otilm.api.interfaces.NoAuthController;
import com.otilm.api.model.core.auth.ProtectedResourceMetadataDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Lets an application discover which authorization servers issue tokens for the API. The document is served at the
 * origin's well-known path, outside the {@code /api} base of the other Core operations, so the operation names the
 * origin as its server.
 */
@RequestMapping("/.well-known/oauth-protected-resource/api")
@Tag(name = "Protected Resource Metadata", description = "OAuth Protected Resource Metadata (RFC 9728) of the API")
public interface ProtectedResourceMetadataController extends NoAuthController {

    @Operation(summary = "Get the OAuth Protected Resource Metadata of the API",
            servers = @Server(url = "https://demo.otilm.com", description = "Platform Demo server"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Metadata retrieved")})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ProtectedResourceMetadataDto getProtectedResourceMetadata();
}
