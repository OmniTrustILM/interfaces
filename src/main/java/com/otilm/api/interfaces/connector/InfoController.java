package com.otilm.api.interfaces.connector;

import com.otilm.api.interfaces.AuthProtectedConnectorController;
import com.otilm.api.model.client.connector.InfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/v1")
@Tag(
        name = "Connector Info",
        description = "Connector Information API. " +
                "Each connector may have multiple functions represented by FunctionGroupCode. " +
                "For each FunctionGroupCode there is a list of implemented end points. " +
                "These endpoints must be according the specified interface, this is validated by the core. " +
                "You can also implement helper end points that are used for callbacks and other relevant operations " +
                "specific to implementation."
)
public interface InfoController extends AuthProtectedConnectorController {

    @GetMapping(
            produces = {"application/json"}
    )
    @Operation(
            summary = "List supported functions of the connector",
            description = "Returns map of functional code and implemented end points"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Functions found"
                    )
    })
    List<InfoResponse> listSupportedFunctions();

}