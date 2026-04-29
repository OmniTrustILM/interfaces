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
                        url = "https://demo.czertainly.online",
                        description = "CZERTAINLY Demo server"
                )
        }
)
@RestController
@SecuritySchemes(value = {
        @SecurityScheme(
                name = OpenApiConfig.BASIC_SECURITY_SCHEME_NAME,
                type = SecuritySchemeType.HTTP,
                scheme = "Basic"
        ),
        @SecurityScheme(
                name = OpenApiConfig.CERTIFICATE_TLS_SECURITY_SCHEME_NAME,
                type = SecuritySchemeType.MUTUALTLS,
                description = "Client certificate authentication"
        ),
        @SecurityScheme(
                name = OpenApiConfig.CONNECTOR_API_KEY_SECURITY_SCHEME_NAME,
                type = SecuritySchemeType.APIKEY,
                in = SecuritySchemeIn.HEADER,
                paramName = "X-API-KEY",
                description = "API Key in header configured for connector"
        ),
        @SecurityScheme(
                name = OpenApiConfig.NO_SECURITY_SCHEME_NAME,
                type = SecuritySchemeType.HTTP,
                scheme = "none",
                description = "No authentication"
        )
})
@SecurityRequirements(value = {
        @SecurityRequirement(name = OpenApiConfig.BASIC_SECURITY_SCHEME_NAME),
        @SecurityRequirement(name = OpenApiConfig.CERTIFICATE_TLS_SECURITY_SCHEME_NAME),
        @SecurityRequirement(name = OpenApiConfig.CONNECTOR_API_KEY_SECURITY_SCHEME_NAME),
        @SecurityRequirement(name = OpenApiConfig.NO_SECURITY_SCHEME_NAME),
})@ApiResponses(
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
                        responseCode = "404",
                        description = "Not Found",
                        content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error",
                        content = @Content
                )
        })
public interface AuthProtectedConnectorController {
}
