package com.otilm.api.interfaces;

import com.otilm.api.config.OpenApiConfig;
import com.otilm.api.model.common.AuthenticationServiceExceptionDto;
import com.otilm.api.model.common.ErrorMessageDto;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.web.bind.annotation.RestController;

@OpenAPIDefinition(
        servers = {
                @Server(
                        url = "https://demo.czertainly.online/api",
                        description = "CZERTAINLY Demo server"
                )
        }
)
@RestController
@SecuritySchemes(value = {
        @SecurityScheme(
                name = OpenApiConfig.BEARER_JWT_SECURITY_SCHEME_NAME,
                type = SecuritySchemeType.HTTP,
                scheme = "Bearer",
                bearerFormat = "JWT"
        ),
        @SecurityScheme(
                name = OpenApiConfig.CERTIFICATE_SECURITY_SCHEME_NAME,
                type = SecuritySchemeType.APIKEY,
                in = SecuritySchemeIn.HEADER,
                paramName = "ssl-client-cert",
                description = "Base64 encoded X.509 certificate passed in header"
        ),
        @SecurityScheme(
                name = OpenApiConfig.SESSION_SECURITY_SCHEME_NAME,
                type = SecuritySchemeType.APIKEY,
                in = SecuritySchemeIn.COOKIE,
                paramName = "czertainly-session", // Name of the cookie containing the session ID
                description = "Session-based authentication with session ID stored in 'czertainly-session' cookie"
        ),
})
@SecurityRequirements(value = {
        @SecurityRequirement(name = OpenApiConfig.BEARER_JWT_SECURITY_SCHEME_NAME),
        @SecurityRequirement(name = OpenApiConfig.CERTIFICATE_SECURITY_SCHEME_NAME),
        @SecurityRequirement(name = OpenApiConfig.SESSION_SECURITY_SCHEME_NAME),
})
@ApiResponses(
        value = {
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request",
                        content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
                ),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized",
                        content = @Content(schema = @Schema())
                ),
                @ApiResponse(
                        responseCode = "403",
                        description = "Forbidden",
                        content = @Content(schema = @Schema(implementation = AuthenticationServiceExceptionDto.class))
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error",
                        content = @Content
                )
        })
public interface AuthProtectedController {
}
