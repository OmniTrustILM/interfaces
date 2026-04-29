package com.otilm.api.interfaces.core.web;

import com.otilm.api.config.OpenApiConfig;
import com.otilm.api.model.common.AuthenticationServiceExceptionDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.core.info.ActuatorHealthDto;
import com.otilm.api.model.core.info.CoreInfoResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1")
@Tag(name = "Info", description = "Platform Application Information API")
@RestController
@SecurityRequirements
@ApiResponses(
        value = {
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request",
                        content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error",
                        content = @Content
                )
        })
public interface InfoController {

    @GetMapping(path = "/info", produces = {"application/json"})
    @Operation(summary = "Get information about running Platform Application")
    @SecurityRequirements(value = {
            @SecurityRequirement(name = OpenApiConfig.BEARER_JWT_SECURITY_SCHEME_NAME),
            @SecurityRequirement(name = OpenApiConfig.CERTIFICATE_SECURITY_SCHEME_NAME),
            @SecurityRequirement(name = OpenApiConfig.SESSION_SECURITY_SCHEME_NAME),
    })
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Platform Application info retrieved"
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
            })
    CoreInfoResponseDto getInfo();

    @GetMapping(path = "/health/liveness", produces = {"application/json"})
    @Operation(summary = "Health liveness check endpoint")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Health liveness checked"
                    )
            })
    ActuatorHealthDto getHealthLiveness();

    @GetMapping(path = "/health/readiness", produces = {"application/json"})
    @Operation(summary = "Health readiness check endpoint")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Health readiness checked"
                    )
            })
    ActuatorHealthDto getHealthReadiness();

}
