package com.otilm.api.interfaces.connector.common.v2;

import com.otilm.api.model.client.connector.v2.InfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/v2/info")
@Tag(
        name = "Connector Info v2",
        description = "Connector Information API. " +
                "Each connector may have multiple functions represented by its connector interfaces. " +
                "For each connector interface there is a list of implemented endpoints. " +
                "These endpoints must be according the specified interface, this is validated by the core. " +
                "You can also implement helper end points that are used for callbacks and other relevant operations " +
                "specific to implementation."
)
public interface InfoController extends NoAuthConnectorController {

    @GetMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(
            summary = "Get Connector Info",
            description = "Returns information about the connector and implemented interfaces."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Connector info retrieved successfully"
                    )
    })
    InfoResponse getConnectorInfo();

}
