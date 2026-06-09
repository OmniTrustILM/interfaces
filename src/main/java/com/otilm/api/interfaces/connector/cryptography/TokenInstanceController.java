package com.otilm.api.interfaces.connector.cryptography;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.interfaces.AuthProtectedConnectorController;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.token.TokenInstanceDto;
import com.otilm.api.model.connector.cryptography.token.TokenInstanceRequestDto;
import com.otilm.api.model.connector.cryptography.token.TokenInstanceStatusDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/cryptographyProvider/tokens")
@Tag(
        name = "Token Management",
        description = "Token Management API is used to manage Token instance created from Cryptography Providers. Token represents connection with key stores that can perform cryptographic operations. It can manage one or more key stores, or it can be used with external key stores, such as vaults, hardware security modules, etc. Token Profile represents particular key store that can be used to execute cryptographic operations and key management through the Token instance."
)
public interface TokenInstanceController extends AuthProtectedConnectorController {

    @Operation(
            summary = "List Token instances"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Token instance list retrieved"
                    )
            })
    @GetMapping(
            produces = {"application/json"}
    )
    List<TokenInstanceDto> listTokenInstances();

    @Operation(
            summary = "Get Token instance"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Token instance retrieved"
                    )
            })
    @GetMapping(
            path = "/{uuid}",
            produces = {"application/json"}
    )
    /**
     * @throws NotFoundException Token instance not found
     */
    TokenInstanceDto getTokenInstance(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid
    ) throws NotFoundException;

    @Operation(
            summary = "Create Token instance"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Token instance created"
                    )
            })
    @PostMapping(
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    /**
     * @throws AlreadyExistException Token instance already exists
     */
    TokenInstanceDto createTokenInstance(
            @RequestBody TokenInstanceRequestDto request
    ) throws AlreadyExistException;

    @Operation(
            summary = "Update Token instance"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Token instance updated"
                    )
            })
    @PostMapping(
            path = "/{uuid}",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    /**
     * @throws NotFoundException Token instance not found
     */
    TokenInstanceDto updateTokenInstance(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid,
            @RequestBody TokenInstanceRequestDto request
    ) throws NotFoundException;

    @Operation(
            summary = "Remove Token instance"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Token instance removed"
                    )
            })
    @DeleteMapping(
            path = "/{uuid}"
    )
    @ResponseStatus(
            HttpStatus.NO_CONTENT
    )
    /**
     * @throws NotFoundException Token instance not found
     */
    void removeTokenInstance(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid
    ) throws NotFoundException;

    @Operation(
            summary = "Get Token instance status",
            description = "Returns the connection status of the Token instance including additional information that might be useful"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Token instance status retrieved"
                    )
            })
    @GetMapping(
            path = "/{uuid}/status",
            produces = {"application/json"}
    )
    /**
     * @throws NotFoundException Token instance not found
     */
    TokenInstanceStatusDto getTokenInstanceStatus(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid
    ) throws NotFoundException;

    @Operation(
            summary = "List Token Profile Attributes"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Token Profile Attributes retrieved"
                    )
            })
    @GetMapping(
            path = "/{uuid}/tokenProfile/attributes",
            produces = {"application/json"}
    )
    /**
     * @throws NotFoundException Token instance not found
     */
    List<BaseAttribute> listTokenProfileAttributes(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid
    ) throws NotFoundException;

    @Operation(
            summary = "Validate Token Profile Attributes"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Token Profile Attributes validated"
                    )
            })
    @PostMapping(
            path = "/{uuid}/tokenProfile/attributes/validate",
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
    void validateTokenProfileAttributes(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid,
            @RequestBody List<RequestAttribute>attributes
    ) throws ValidationException, NotFoundException;

    /////////////////////////////////////////////////////////////////////////////////
    // activation of token
    /// //////////////////////////////////////////////////////////////////////////////

    @Operation(
            summary = "List Token activation Attributes"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Token activation Attributes retrieved"
                    )
            })
    @GetMapping(
            path = "/{uuid}/activate/attributes",
            produces = {"application/json"}
    )
    /**
     * @throws NotFoundException Token instance not found
     */
    List<BaseAttribute> listTokenInstanceActivationAttributes(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid
    ) throws NotFoundException;

    @Operation(
            summary = "Validate Token activation Attributes"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Token activation Attributes validated"
                    )
            })
    @PostMapping(
            path = "/{uuid}/activate/attributes/validate",
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
    void validateTokenInstanceActivationAttributes(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid,
            @RequestBody List<RequestAttribute>attributes
    ) throws ValidationException, NotFoundException;

    @Operation(
            summary = "Activate Token"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Token activated"
                    )
            })
    @PatchMapping(
            path = "/{uuid}/activate",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    @ResponseStatus(
            value = HttpStatus.NO_CONTENT
    )
    /**
     * @throws NotFoundException Token instance not found
     */
    void activateTokenInstance(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid,
            @RequestBody List<RequestAttribute>attributes
    ) throws NotFoundException;

    @Operation(
            summary = "Deactivate Token"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Token deactivated"
                    )
            })
    @PatchMapping(
            path = "/{uuid}/deactivate",
            produces = {"application/json"}
    )
    /**
     * @throws NotFoundException Token instance not found
     */
    void deactivateTokenInstance(
            @Parameter(description = "Token instance UUID") @PathVariable String uuid
    ) throws NotFoundException;

}
