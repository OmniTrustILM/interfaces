package com.otilm.api.interfaces.connector.signing;

import com.otilm.api.exception.ValidationException;
import com.otilm.api.interfaces.connector.common.v2.AuthProtectedConnectorController;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.signatures.formatting.FormatDtbsRequestDto;
import com.otilm.api.model.connector.signatures.formatting.FormatDtbsResponseDto;
import com.otilm.api.model.connector.signatures.formatting.FormatResponseRequestDto;
import com.otilm.api.model.connector.signatures.formatting.FormattedResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/signatureProvider/formatting")
@Tag(
        name = "Signature Formatting Provider",
        description = "Signature Formatting Provider API defines operations for protocol-specific formatting of digital signing requests. " +
                "The provider is stateless and handles the conversion between raw signing material and " +
                "protocol-specific formats (e.g. TSA TimeStampToken, AdES signature containers)."
)
public interface SignatureFormattingController extends AuthProtectedConnectorController {

    @Operation(
            summary = "List Formatting Attributes",
            operationId = "listFormattingAttributes"
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
    List<BaseAttribute> listFormattingAttributes();

    @Operation(
            summary = "Compute data-to-be-signed bytes",
            operationId = "formatDtbs"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Data-to-be-signed bytes computed"
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
            path = "/formatDtbs",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    FormatDtbsResponseDto formatDtbs(@Valid @RequestBody FormatDtbsRequestDto request) throws ValidationException;

    @Operation(
            summary = "Assemble final formatted output",
            operationId = "formatSigningResponse"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Formatted response assembled"
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
            path = "/formatResponse",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    FormattedResponseDto formatSigningResponse(@Valid @RequestBody FormatResponseRequestDto request) throws ValidationException;
}
