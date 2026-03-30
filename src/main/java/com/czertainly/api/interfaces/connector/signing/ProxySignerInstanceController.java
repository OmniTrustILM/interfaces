package com.czertainly.api.interfaces.connector.signing;

import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.interfaces.connector.common.v2.AuthProtectedConnectorController;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/signatureProvider/signers")
@Tag(
        name = "Signer Instance",
        description = "Signer Instance API manages connectivity to an external signing service (e.g. SignServer). " +
                "All endpoints are stateless — connection attributes are supplied on every request."
)
public interface ProxySignerInstanceController extends AuthProtectedConnectorController {

    @Operation(
            summary = "List Signer Attributes",
            operationId = "listSignerAttributes"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Attributes retrieved"
                    )
            }
    )
    @GetMapping(
            path = "/attributes",
            produces = {"application/json"}
    )
    List<BaseAttribute> listSignerAttributes();

    @Operation(
            summary = "Validate Signer Attributes",
            operationId = "validateSignerAttributes"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Attributes validated"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}
                            )
                    )
            }
    )
    @PostMapping(
            path = "/attributes/validate",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void validateSignerAttributes(@RequestBody List<RequestAttribute> attributes) throws ValidationException;

    @Operation(
            summary = "Check Signer Connection",
            operationId = "checkSignerConnection",
            description = "Tests live connectivity to the external signing service using the supplied connection attributes. " +
                    "Core calls validateSignerAttributes first; this endpoint must not duplicate validation logic."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Connection successful"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}
                            )
                    )
            }
    )
    @PostMapping(
            path = "/connectionCheck",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void checkSignerConnection(@RequestBody List<RequestAttribute> attributes) throws ValidationException;

}
