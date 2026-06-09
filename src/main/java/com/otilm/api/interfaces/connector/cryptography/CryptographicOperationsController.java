package com.otilm.api.interfaces.connector.cryptography;

import com.otilm.api.exception.NotFoundException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.interfaces.AuthProtectedConnectorController;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.operations.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@RequestMapping("/v1/cryptographyProvider/tokens/{uuid}/keys")
@Tag(
        name = "Cryptographic Operations",
        description = "Cryptographic Operations API defines operations that can be executed on existing cryptographic Keys."
)
public interface CryptographicOperationsController extends AuthProtectedConnectorController {

    /////////////////////////////////////////////////////////////////////////////////
    // cipher operations
    /////////////////////////////////////////////////////////////////////////////////

    // ------------------------------------------------------------------------------
    // Attributes for Cipher and Signature is controlled by core
    // ------------------------------------------------------------------------------

    @Operation(
            summary = "Encrypt data using a Key"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Data encrypted"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @PostMapping(
            path = "/{keyUuid}/encrypt",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    /**
     * @throws NotFoundException Token instance or Key not found
     */
    EncryptDataResponseDto encryptData(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid,
            @Parameter(description = "Key UUID") @PathVariable String keyUuid,
            @RequestBody CipherDataRequestDto request
    ) throws NotFoundException;

    @Operation(
            summary = "Decrypt data using a Key"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Data decrypted"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @PostMapping(
            path = "/{keyUuid}/decrypt",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    /**
     * @throws NotFoundException Token instance or Key not found
     */
    DecryptDataResponseDto decryptData(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid,
            @Parameter(description = "Key UUID") @PathVariable String keyUuid,
            @RequestBody CipherDataRequestDto request
    ) throws NotFoundException;

    /////////////////////////////////////////////////////////////////////////////////
    // signature operations
    /////////////////////////////////////////////////////////////////////////////////

    @Operation(
            summary = "Sign data using a Key"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Data signed"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @PostMapping(
            path = "/{keyUuid}/sign",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    /**
     * @throws NotFoundException Token instance or Key not found
     */
    SignDataResponseDto signData(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid,
            @Parameter(description = "Key UUID") @PathVariable String keyUuid,
            @RequestBody SignDataRequestDto request
    ) throws NotFoundException;

    @Operation(
            summary = "Verify data using a Key"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Data decrypted"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @PostMapping(
            path = "/{keyUuid}/verify",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    /**
     * @throws NotFoundException Token instance or Key not found
     */
    VerifyDataResponseDto verifyData(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid,
            @Parameter(description = "Key UUID") @PathVariable String keyUuid,
            @RequestBody VerifyDataRequestDto request
    ) throws NotFoundException;

    /////////////////////////////////////////////////////////////////////////////////
    // generate random operations
    /////////////////////////////////////////////////////////////////////////////////

    @Operation(
            summary = "List of random generator Attributes"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of Attributes retrieved"
                    )
            })
    @GetMapping(
            path = "/random/attributes",
            produces = {"application/json"}
    )
    /**
     * @throws NotFoundException Token instance not found
     */
    List<BaseAttribute> listRandomAttributes(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid
    ) throws NotFoundException;

    @Operation(
            summary = "Validate random generator Attributes"
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
                            ))
            })
    @PostMapping(
            path = "/random/attributes/validate",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    @ResponseStatus(
            value = HttpStatus.NO_CONTENT
    )
    /**
     * @throws NotFoundException Token instance not found
     * @throws ValidationException Invalid Attributes
     */
    void validateRandomAttributes(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid,
            @RequestBody List<RequestAttribute>attributes
    ) throws NotFoundException, ValidationException;

    @Operation(
            summary = "Generate random data"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Random data generated"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @PostMapping(
            path = "/random",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    /**
     * @throws NotFoundException Token instance not found
     */
    RandomDataResponseDto randomData(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid,
            @RequestBody RandomDataRequestDto request
    ) throws NotFoundException;

}
