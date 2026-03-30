package com.czertainly.api.interfaces.connector.signing;

import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.interfaces.connector.common.v2.AuthProtectedConnectorController;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.signatures.signer.ProxySignResponseDto;
import com.czertainly.api.model.connector.signatures.signer.ProxySignatureValidateResponseDto;
import com.czertainly.api.model.connector.signatures.signer.ProxySignRequestDto;
import com.czertainly.api.model.connector.signatures.signer.ProxySignatureValidateRequestDto;
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

@RequestMapping("/v1/signatureProvider/signing")
@Tag(
        name = "Signing",
        description = "Signing API executes sign and verify operations against an external signing service. " +
                "Connection attributes are supplied on every request."
)
public interface ProxySigningController extends AuthProtectedConnectorController {

    @Operation(
            summary = "List Sign Attributes",
            operationId = "listSignAttributes",
            description = "Returns the attribute definitions for a sign operation."
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
            path = "/sign/attributes",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    List<BaseAttribute> listSignAttributes();

    @Operation(
            summary = "Validate Sign Attributes",
            operationId = "validateSignAttributes"
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
            path = "/sign/attributes/validate",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void validateSignAttributes(@RequestBody List<RequestAttribute> attributes) throws ValidationException;

    @Operation(
            summary = "Sign",
            operationId = "sign",
            description = "Executes a signing operation."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Signing operation completed"
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
            path = "/sign",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    ProxySignResponseDto sign(@RequestBody ProxySignRequestDto request);

    @Operation(
            summary = "List Validate Attributes",
            operationId = "listValidateAttributes",
            description = "Returns the attribute definitions for a validate operation. " +
                    "Accepts connectionAttributes so the connector can return dynamic lists."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Attributes retrieved"
                    )
            }
    )
    @PostMapping(
            path = "/validate/attributes",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    List<BaseAttribute> listValidationAttributes(@RequestBody List<RequestAttribute> connectionAttributes);

    @Operation(
            summary = "Validate Validation Attributes",
            operationId = "validateValidateAttributes"
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
            path = "/validate/attributes/validate",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void validateValidationAttributes(@RequestBody List<RequestAttribute> attributes) throws ValidationException;

    @Operation(
            summary = "Validate",
            operationId = "validate",
            description = "Executes a validation operation."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Validation operation completed"
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
            path = "/validate",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    ProxySignatureValidateResponseDto validate(@RequestBody ProxySignatureValidateRequestDto request);

}
