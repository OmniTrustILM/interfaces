package com.otilm.api.interfaces.core.web.v2;

import com.otilm.api.interfaces.NoAuthController;
import com.otilm.api.model.core.auth.LoginProviderDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/v2/oauth2/providers")
@Tag(name = "OAuth2 Login Management v2", description = "OAuth2 Login Management v2 API")
public interface OAuth2LoginController extends NoAuthController {
    @Operation(summary = "Get available OAuth2 authentication providers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved available login providers"),
            @ApiResponse(responseCode = "400", description = "Error during authentication")
    })
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    List<LoginProviderDto> getOAuth2Providers(@RequestParam(value = "error", required = false) String error);

    @Operation(
            summary = "Login with OAuth2 provider",
            description = "Initiates OAuth2 login flow with the specified provider. Returns a redirect response to the provider's authentication page."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirect to provider's authentication page"),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid provider or authentication error")
    })
    @GetMapping("/{provider}/login")
    ResponseEntity<Void> loginWithProvider(
            @Parameter(description = "Name of the OAuth2 authentication provider", required = true)
            @PathVariable String provider,
            @Parameter(description = "Redirect URL to return to after successful authentication")
            @RequestParam(value = "redirect", required = false) String redirect
    );
}
