package com.czertainly.api.interfaces.core.web;

import com.czertainly.api.exception.AlreadyExistException;
import com.czertainly.api.exception.AttributeException;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.NotFoundException;
import com.czertainly.api.interfaces.AuthProtectedController;
import com.czertainly.api.model.client.certificate.SearchRequestDto;
import com.czertainly.api.model.common.ErrorMessageDto;
import com.czertainly.api.model.common.PaginationResponseDto;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.secrets.SecretType;
import com.czertainly.api.model.core.search.SearchFieldDataByGroupDto;
import com.czertainly.api.model.core.vaultprofile.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/v1")
@Tag(name = "Vault Profile Management", description = "APIs for managing Vault Profiles")
@ApiResponses(
        value = {
                @ApiResponse(
                        responseCode = "404",
                        description = "Not Found",
                        content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
                ),
                @ApiResponse(
                        responseCode = "502",
                        description = "Connector Error",
                        content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
                ),
                @ApiResponse(
                        responseCode = "503",
                        description = "Connector Communication Error",
                        content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
                ),
        })
public interface VaultProfileController extends AuthProtectedController {

    @Operation(summary = "List Vault Profiles")
    @ApiResponse(responseCode = "200", description = "List of Vault Profiles retrieved")
    @PostMapping(path = "/vaultProfiles/list", consumes = {"application/json"}, produces = {"application/json"})
    PaginationResponseDto<VaultProfileDto> listVaultProfiles(@RequestBody SearchRequestDto searchRequest);

    @Operation(summary = "Get details of a Vault Profile")
    @ApiResponse(responseCode = "200", description = "Vault Profile details retrieved")
    @GetMapping(path = "/vaults/{vaultUuid}/vaultProfiles/{vaultProfileUuid}", consumes = {"application/json"}, produces = {"application/json"})
    VaultProfileDetailDto getVaultProfileDetails(@Parameter(description = "UUID of Vault Instance") @PathVariable UUID vaultUuid, @Parameter(description = "UUID of vault profile") @PathVariable UUID vaultProfileUuid) throws NotFoundException;

    @Operation(summary = "Update a Vault Profile")
    @ApiResponse(responseCode = "200", description = "Vault Profile updated")
    @PutMapping(path = "/vaults/{vaultUuid}/vaultProfiles/{vaultProfileUuid}", consumes = {"application/json"}, produces = {"application/json"})
    VaultProfileDetailDto updateVaultProfile(@Parameter(description = "UUID of Vault Instance") @PathVariable UUID vaultUuid, @Parameter(description = "UUID of vault profile") @PathVariable UUID vaultProfileUuid, @RequestBody VaultProfileUpdateRequestDto vaultProfileUpdateRequest) throws NotFoundException, AttributeException;

    @Operation(summary = "Delete a Vault Profile")
    @ApiResponse(responseCode = "204", description = "Vault Profile deleted")
    @DeleteMapping(path = "/vaults/{vaultUuid}/vaultProfiles/{vaultProfileUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteVaultProfile(@Parameter(description = "UUID of Vault Instance") @PathVariable UUID vaultUuid, @Parameter(description = "UUID of vault profile") @PathVariable UUID vaultProfileUuid) throws NotFoundException;

    @Operation(summary = "Create a Vault Profile")
    @ApiResponse(responseCode = "201", description = "Vault Profile created")
    @PostMapping(path = "/vaults/{vaultUuid}/vaultProfiles", consumes = {"application/json"}, produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    VaultProfileDetailDto createVaultProfile(@Parameter(description = "UUID of Vault Instance") @PathVariable UUID vaultUuid, @RequestBody @Valid VaultProfileRequestDto vaultProfileRequest) throws NotFoundException, AttributeException, AlreadyExistException;

    @Operation(summary = "Enable a Vault Profile")
    @ApiResponse(responseCode = "204", description = "Vault Profile enabled")
    @PatchMapping(path = "/vaults/{vaultUuid}/vaultProfiles/{vaultProfileUuid}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void enableVaultProfile(@Parameter(description = "UUID of Vault Instance") @PathVariable UUID vaultUuid, @Parameter(description = "UUID of vault profile") @PathVariable UUID vaultProfileUuid) throws NotFoundException;

    @Operation(summary = "Disable a Vault Profile")
    @ApiResponse(responseCode = "204", description = "Vault Profile disabled")
    @PatchMapping(path = "/vaults/{vaultUuid}/vaultProfiles/{vaultProfileUuid}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void disableVaultProfile(@Parameter(description = "UUID of Vault Instance") @PathVariable UUID vaultUuid, @Parameter(description = "UUID of vault profile") @PathVariable UUID vaultProfileUuid) throws NotFoundException;

    @Operation(summary = "List attributes for creating a secret in a Vault Profile")
    @ApiResponse(responseCode = "200", description = "List of attributes for creating a secret in a Vault Profile retrieved")
    @GetMapping(path = "/vaults/{vaultUuid}/vaultProfiles/{vaultProfileUuid}/secrets/{secretType}/attributes", produces = {"application/json"})
    List<BaseAttribute> getAttributesForCreatingSecret(@Parameter(description = "UUID of Vault Instance") @PathVariable UUID vaultUuid, @Parameter(description = "UUID of vault profile") @PathVariable UUID vaultProfileUuid, @Parameter(description = "Type of the secret") @PathVariable SecretType secretType) throws ConnectorException, NotFoundException;

    @Operation(operationId = "getVaultProfileSearchableFields", summary = "List search filters for Vault Profiles")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of search filters retrieved")})
    @GetMapping(path = "/vaultProfiles/search", produces = {"application/json"})
    List<SearchFieldDataByGroupDto> getSearchableFieldInformation();

}
