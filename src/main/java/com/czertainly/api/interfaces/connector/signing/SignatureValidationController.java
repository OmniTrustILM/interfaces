package com.czertainly.api.interfaces.connector.signing;

import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.interfaces.AuthProtectedConnectorController;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.signatures.validation.SignatureValidationRequestDto;
import com.czertainly.api.model.connector.signatures.validation.SignatureValidationResultDto;
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

@RequestMapping("/v1/signatureProvider/validation")
@Tag(
        name = "Signature Validation",
        description = "Signature Validation API defines operations for ILM-native signature validation. " +
                "The connector performs full validation — envelope parsing, structural checks, policy compliance, " +
                "and cryptographic verification using the public key from the signer certificate."
)
public interface SignatureValidationController extends AuthProtectedConnectorController {

    @Operation(
            summary = "List Validation Attributes"
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
    List<BaseAttribute> listValidationAttributes();

    @Operation(
            summary = "Validate Validation Attributes"
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
    void validateValidationAttributes(@RequestBody List<RequestAttribute> attributes) throws ValidationException;

    @Operation(
            summary = "Validate signature",
            description = "Performs full signature validation (structural and cryptographic)."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Validation completed. Check status field for the outcome."
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
    SignatureValidationResultDto validate(@RequestBody SignatureValidationRequestDto request);
}
