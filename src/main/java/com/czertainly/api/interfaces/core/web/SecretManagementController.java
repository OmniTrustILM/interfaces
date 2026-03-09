package com.czertainly.api.interfaces.core.web;

import com.czertainly.api.exception.*;
import com.czertainly.api.interfaces.AuthProtectedController;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.client.certificate.SearchRequestDto;
import com.czertainly.api.model.common.ErrorMessageDto;
import com.czertainly.api.model.common.PaginationResponseDto;
import com.czertainly.api.model.connector.secrets.content.SecretContent;
import com.czertainly.api.model.core.search.SearchFieldDataByGroupDto;
import com.czertainly.api.model.core.secret.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

@RequestMapping("/v1")
@Tag(name = "Secret Management", description = "APIs for managing secrets, including creation, retrieval, updating, and deletion of secrets.")
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
public interface SecretManagementController extends AuthProtectedController {

    @Operation(operationId = "getSecretSearchableFields", summary = "List search filters for secrets")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of search filters retrieved")})
    @GetMapping(path = "/secrets/search", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<SearchFieldDataByGroupDto> getSearchableFieldInformation();

    @Operation(summary = "List secrets")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of secrets retrieved")})
    @PostMapping(path = "/secrets", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    PaginationResponseDto<SecretDto> listSecrets(@RequestBody SearchRequestDto searchRequest);

    @Operation(summary = "Get secret details")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Secret details retrieved")})
    @GetMapping(path = "/secrets/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    SecretDetailDto getSecretDetails(@Parameter(description = "UUID of the secret") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(summary = "Get secret versions")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Secret versions retrieved")})
    @GetMapping(path = "/secrets/{uuid}/versions", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<SecretVersionDto> getSecretVersions(@Parameter(description = "UUID of the secret") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(summary = "Get secret content")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Secret content retrieved")})
    @GetMapping(path = "/secrets/{uuid}/content", produces = {MediaType.APPLICATION_JSON_VALUE})
    SecretContent getSecretContent(@Parameter(description = "UUID of the secret") @PathVariable UUID uuid) throws NotFoundException, ConnectorException, NoSuchAlgorithmException;

    @Operation(summary = "Create a new secret")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Secret created successfully")})
    @PostMapping(path = "/vaults/{vaultUuid}/vaultProfiles/{vaultProfileUuid}/secrets", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    SecretDetailDto createSecret(@RequestBody @Valid SecretRequestDto secretRequest, @Parameter(description = "UUID of vault profile") @PathVariable UUID vaultProfileUuid, @Parameter(description = "UUID of vault instance") @PathVariable UUID vaultUuid) throws NotFoundException, AttributeException, AlreadyExistException, ConnectorException;

    @Operation(summary = "Update an existing secret")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Secret updated successfully")})
    @PutMapping(path = "/secrets/{uuid}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    SecretDetailDto updateSecret(@Parameter(description = "UUID of the secret") @PathVariable UUID uuid, @RequestBody SecretUpdateRequestDto secretRequest) throws NotFoundException, AttributeException, ConnectorException;

    @Operation(summary = "Delete a secret")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Secret deleted successfully")})
    @DeleteMapping(path = "/secrets/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteSecret(@Parameter(description = "UUID of the secret") @PathVariable UUID uuid) throws NotFoundException, ConnectorException;

    @Operation(summary = "Enable a secret")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Secret enabled successfully")})
    @PatchMapping(path = "/secrets/{uuid}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void enableSecret(@Parameter(description = "UUID of the secret") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(summary = "Disable a secret")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Secret disabled successfully")})
    @PatchMapping(path = "/secrets/{uuid}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void disableSecret(@Parameter(description = "UUID of the secret") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(summary = "Add vault profile to secret")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Vault profile added to secret successfully")})
    @PatchMapping(path = "/secrets/{uuid}/syncVaultProfiles/{vaultProfileUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void addVaultProfileToSecret(@Parameter(description = "UUID of the secret") @PathVariable UUID uuid, @Parameter(description = "UUID of the vault profile") @PathVariable UUID vaultProfileUuid, @RequestBody List<RequestAttribute> createSecretAttributes) throws NotFoundException, ConnectorException, AttributeException;

    @Operation(summary = "Remove vault profile from secret")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Vault profile removed from secret successfully")})
    @DeleteMapping(path = "/secrets/{uuid}/syncVaultProfiles/{vaultProfileUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeVaultProfileFromSecret(@Parameter(description = "UUID of the secret") @PathVariable UUID uuid, @Parameter(description = "UUID of the vault profile") @PathVariable UUID vaultProfileUuid) throws NotFoundException, ConnectorException;

    @Operation(summary = "Update Secret Objects")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Secret objects updated")})
    @PatchMapping(path = "/secrets/{uuid}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateSecretObjects(@Parameter(description = "Secret UUID") @PathVariable UUID uuid, @RequestBody SecretUpdateObjectsDto request) throws NotFoundException, ConnectorException, AttributeException;

}
