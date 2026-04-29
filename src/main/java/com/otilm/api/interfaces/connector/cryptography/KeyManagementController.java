package com.otilm.api.interfaces.connector.cryptography;

import com.otilm.api.exception.NotFoundException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.interfaces.AuthProtectedConnectorController;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.key.CreateKeyRequestDto;
import com.otilm.api.model.connector.cryptography.key.KeyDataResponseDto;
import com.otilm.api.model.connector.cryptography.key.KeyPairDataResponseDto;
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
        name = "Key Management",
        description = "Key Management API for cryptographic key management operations. Each key is managed by a specific token that is connected through the Token instance."
)
public interface KeyManagementController extends AuthProtectedConnectorController {

    @Operation(
            summary = "List of Attributes to create a Secret Key"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of Attributes retrieved"
                    )
            })
    @GetMapping(
            path = "/secret/attributes",
            produces = {"application/json"}
    )
    /**
     * @throws NotFoundException Token instance not found
     */
    List<BaseAttribute> listCreateSecretKeyAttributes(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid
    ) throws NotFoundException;

    @Operation(
            summary = "Validate list of Attributes to create a Secret Key"
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
                                    examples={@ExampleObject(value="[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @PostMapping(
            path = "/secret/attributes/validate",
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
    void validateCreateSecretKeyAttributes(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid,
            @RequestBody List<RequestAttribute>attributes
    ) throws NotFoundException, ValidationException;

    @Operation(
            summary = "Create a Secret Key"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Key created"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples={@ExampleObject(value="[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @PostMapping(
            path = "/secret",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    /**
     * @throws NotFoundException Token instance not found
     */
    KeyDataResponseDto createSecretKey(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid,
            @RequestBody CreateKeyRequestDto request
    ) throws NotFoundException;

    @Operation(
            summary = "List of Attributes to create a Key Pair"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of Attributes retrieved"
                    )
            })
    @GetMapping(
            path = "/pair/attributes",
            produces = {"application/json"}
    )
    /**
     * @throws NotFoundException Token instance not found
     */
    List<BaseAttribute> listCreateKeyPairAttributes(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid
    ) throws NotFoundException;

    @Operation(
            summary = "Validate list of Attributes to create a Key Pair"
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
                                    examples={@ExampleObject(value="[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @PostMapping(
            path = "/pair/attributes/validate",
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
    void validateCreateKeyPairAttributes(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid,
            @RequestBody List<RequestAttribute>attributes
    ) throws NotFoundException, ValidationException;

    @Operation(
            summary = "Create a Key Pair, Public and Private Key"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Key Pair created"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples={@ExampleObject(value="[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @PostMapping(
            path = "/pair",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    /**
     * @throws NotFoundException Token instance not found
     */
    KeyPairDataResponseDto createKeyPair(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid,
            @RequestBody CreateKeyRequestDto request
    ) throws NotFoundException;

    @Operation(
            summary = "List Keys for the Token instance"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Keys listed"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples={@ExampleObject(value="[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @GetMapping(
            produces = {"application/json"}
    )
    /**
     * @throws NotFoundException Token instance not found
     */
    List<KeyDataResponseDto> listKeys(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid
    ) throws NotFoundException;

    @Operation(
            summary = "Get details about the Key"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Key data retrieved"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples={@ExampleObject(value="[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @GetMapping(
            path = "/{keyUuid}",
            produces = {"application/json"}
    )
    /**
     * @throws NotFoundException Token instance or Key not found
     */
    KeyDataResponseDto getKey(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid,
            @Parameter(description = "Key UUID") @PathVariable String keyUuid
    ) throws NotFoundException;

    @Operation(
            summary = "Destroy a Key"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Key destroyed"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples={@ExampleObject(value="[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @DeleteMapping(
            path = "/{keyUuid}"
    )
    @ResponseStatus(
            value = HttpStatus.NO_CONTENT
    )
    /**
     * @throws NotFoundException Token instance or Key not found
     */
    void destroyKey(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid,
            @Parameter(description = "Key UUID") @PathVariable String keyUuid
    ) throws NotFoundException;

}
