package com.otilm.api.interfaces.connector.common.v2;

import com.otilm.api.model.client.connector.v2.HealthInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/v2/health")
@Tag(
        name = "Health check v2",
        description = "Connector Health check API. " +
                "Connector returns own status and in some cases " +
                "can return status of components on which it depends like database, HSM and so on."
)
public interface HealthController extends NoAuthConnectorController {

    @GetMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(
            summary = "Health check"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Health check retrieved successfully"
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Service Unavailable. Health check indicates service or its component is unavailable",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = HealthInfo.class)
                            )
                    )
            })
    HealthInfo checkHealth();

    @GetMapping(
            path = "/liveness",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(
            summary = "Health liveness check"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Health liveness check retrieved successfully"
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Service Unavailable. Health liveness check indicates service or its component is not live",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = HealthInfo.class)
                            )
                    )
            })
    HealthInfo checkHealthLiveness();

    @GetMapping(
            path = "/readiness",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(
            summary = "Health readiness check"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Health readiness check retrieved successfully"
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Service Unavailable. Health readiness check indicates service or its component is not ready",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = HealthInfo.class)
                            )
                    )
            })
    HealthInfo checkHealthReadiness();

}
