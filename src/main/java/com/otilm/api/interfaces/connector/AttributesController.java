package com.otilm.api.interfaces.connector;

import com.otilm.api.exception.ValidationException;
import com.otilm.api.interfaces.AuthProtectedConnectorController;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.core.connector.FunctionGroupCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/{functionalGroup}/{kind}/attributes")
@Tag(
        name = "Connector Attributes",
        description = "Connector Attributes API. " +
                "Provides information about supported Attributes of the connector. " +
                "Attributes are specific to implementation and gives information about the " +
                "data that can be exchanged and properly parsed by the connector. " +
                "Part of this API is validation of the Attributes."
)
public interface AttributesController extends AuthProtectedConnectorController {

    @GetMapping(
            produces = {"application/json"}
    )
    @Operation(
            summary = "List available Attributes",
            parameters = {
                    @Parameter(name = "functionalGroup", description = "Function Group", in = ParameterIn.PATH, schema = @Schema(implementation = FunctionGroupCode.class))
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Attributes retrieved"
                    )
            }
    )
    List<BaseAttribute> listAttributeDefinitions(@Parameter(description = "Kind") @PathVariable String kind);

    @PostMapping(
            path = "/validate",
            consumes = {"application/json"},
            produces = { "application/json" }

    )
    @Operation(
            summary = "Validate Attributes",
            parameters = {
                    @Parameter(name = "functionalGroup", description = "Function Group", in = ParameterIn.PATH, schema = @Schema(implementation = FunctionGroupCode.class))
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Attribute validation completed"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Attribute validation failed",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class, examples = {"Attribute Validation error message"})),
                                    examples={@ExampleObject(value="[\"Error Message 1\",\"Error Message 2\"]")})

                    )
            }
    )
    void validateAttributes(@Parameter(required = true, description = "Kind") @PathVariable String kind, @RequestBody List<RequestAttribute>attributes) throws ValidationException;

}