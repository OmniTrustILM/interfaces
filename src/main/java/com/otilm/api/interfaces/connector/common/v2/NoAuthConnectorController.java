package com.otilm.api.interfaces.connector.common.v2;

import com.otilm.api.model.common.error.ProblemDetailExtended;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.http.MediaType;
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
@SecurityRequirements
@ApiResponses(
        value = {
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                schema = @Schema(implementation = ProblemDetailExtended.class)
                        )
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Not Found. Endpoint not found or not implemented",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                schema = @Schema(implementation = ProblemDetailExtended.class)
                        )
                )
        })
public interface NoAuthConnectorController {
}
