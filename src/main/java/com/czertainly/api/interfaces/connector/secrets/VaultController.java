package com.czertainly.api.interfaces.connector.secrets;

import com.czertainly.api.exception.NotFoundException;
import com.czertainly.api.interfaces.connector.common.v2.AuthProtectedConnectorController;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.common.error.ProblemDetailExtended;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/secretProvider")
@Tag(name = "Vault Management", description = "Vault Management API")
public interface VaultController extends AuthProtectedConnectorController {

    @PostMapping(path = "/vaults", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(
            summary = "Check connection to Vault Instance"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Vault instance connected"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable Entity",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service Unavailable",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)
                    )
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void checkVaultConnection(@Parameter(description = "Vault attributes") @RequestBody List<RequestAttribute> attributes) throws NotFoundException;

    @Operation(
            summary = "List Vault Attributes"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vault Attributes retrieved"
            )
    })
    @GetMapping(path = "/vaults/attributes", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BaseAttribute> listVaultAttributes() throws NotFoundException;

    @Operation(
            summary = "List Vault Profile Attributes"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vault Profile Attributes retrieved"
            )
    })
    @PostMapping(path = "/vaultProfiles/attributes", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BaseAttribute> listVaultProfileAttributes(@Parameter(description = "Vault attributes") @RequestBody List<RequestAttribute> attributes) throws NotFoundException;

}