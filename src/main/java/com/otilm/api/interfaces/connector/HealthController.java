package com.otilm.api.interfaces.connector;

import com.otilm.api.interfaces.AuthProtectedConnectorController;
import com.otilm.api.model.common.HealthDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/v1/health")
@Tag(
        name = "Health check",
        description = "Connector Health check API. " +
                "Connector returns own status and in some cases " +
                "can return status of services on which it depends like database, HSM and so on."
)
public interface HealthController extends AuthProtectedConnectorController {

    @GetMapping(
            produces = {"application/json"}
    )
    @Operation(
            summary = "Health check"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Health check completed successfully"
                    )
    })
    HealthDto checkHealth();

}