package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.AttributeException;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.certificate.SearchRequestDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.common.PaginationResponseDto;
import com.otilm.api.model.connector.secrets.content.SecretContent;
import com.otilm.api.model.core.search.ConfigurableColumnsDocs;
import com.otilm.api.model.core.search.SearchFieldDataByGroupDto;
import com.otilm.api.model.core.secret.SecretDetailDto;
import com.otilm.api.model.core.secret.SecretDto;
import com.otilm.api.model.core.secret.SecretRequestDto;
import com.otilm.api.model.core.secret.SecretUpdateObjectsDto;
import com.otilm.api.model.core.secret.SecretUpdateRequestDto;
import com.otilm.api.model.core.secret.SecretVersionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/v1")
@Tag(name = "Secret Management",
        description = "APIs for managing secrets, including creation, retrieval, updating, and deletion of secrets.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Not Found",
                content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
        @ApiResponse(responseCode = "502", description = "Connector Error",
                content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
        @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
public interface SecretManagementController extends AuthProtectedController {

    @Operation(operationId = "getSecretSearchableFields", summary = "List search filters for secrets",
            description = ConfigurableColumnsDocs.CATALOGUE_FLAGS)
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of search filters retrieved")})
    @GetMapping(path = "/secrets/search", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<SearchFieldDataByGroupDto> getSearchableFieldInformation();

    @Operation(summary = "List secrets",
            description = ConfigurableColumnsDocs.SORT_AND_COLUMNS + ConfigurableColumnsDocs.ATTRIBUTE_PROJECTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of secrets retrieved"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Content",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(path = "/secrets", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    PaginationResponseDto<SecretDto> listSecrets(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(implementation = SearchRequestDto.class),
                    examples = {@ExampleObject(name = "With ordering and columns", value = """
                            {
                              "pageNumber": 1,
                              "itemsPerPage": 10,
                              "filters": [],
                              "sort": {"fieldSource": "property", "fieldIdentifier": "SECRET_NAME", "direction": "asc"},
                              "columns": [
                                {"fieldSource": "property", "fieldIdentifier": "SECRET_NAME"},
                                {"fieldSource": "property", "fieldIdentifier": "SECRET_STATE"},
                                {"fieldSource": "property", "fieldIdentifier": "SECRET_OWNER"}
                              ]
                            }""")})) @Valid @RequestBody SearchRequestDto searchRequest);

    @Operation(summary = "Get secret details")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Secret details retrieved")})
    @GetMapping(path = "/secrets/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    SecretDetailDto getSecretDetails(@Parameter(description = "UUID of the secret") @PathVariable UUID uuid)
            throws NotFoundException;

    @Operation(summary = "Get secret versions")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Secret versions retrieved")})
    @GetMapping(path = "/secrets/{uuid}/versions", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<SecretVersionDto> getSecretVersions(@Parameter(description = "UUID of the secret") @PathVariable UUID uuid)
            throws NotFoundException;

    @Operation(summary = "Get secret content")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Secret content retrieved")})
    @GetMapping(path = "/secrets/{uuid}/content", produces = {MediaType.APPLICATION_JSON_VALUE})
    SecretContent getSecretContent(@Parameter(description = "UUID of the secret") @PathVariable UUID uuid)
            throws NotFoundException, ConnectorException, AttributeException;

    @Operation(summary = "Create a new secret")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Secret created successfully")})
    @PostMapping(path = "/vaults/{vaultUuid}/vaultProfiles/{vaultProfileUuid}/secrets",
            consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    SecretDetailDto createSecret(@RequestBody @Valid SecretRequestDto secretRequest,
            @Parameter(description = "UUID of vault profile") @PathVariable UUID vaultProfileUuid,
            @Parameter(description = "UUID of vault instance") @PathVariable UUID vaultUuid)
            throws NotFoundException, AttributeException, AlreadyExistException, ConnectorException;

    @Operation(summary = "Update an existing secret")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Secret updated successfully")})
    @PutMapping(path = "/secrets/{uuid}", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    SecretDetailDto updateSecret(@Parameter(description = "UUID of the secret") @PathVariable UUID uuid,
            @RequestBody SecretUpdateRequestDto secretRequest)
            throws NotFoundException, AttributeException, ConnectorException;

    @Operation(summary = "Delete a secret")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Secret deleted successfully")})
    @DeleteMapping(path = "/secrets/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteSecret(@Parameter(description = "UUID of the secret") @PathVariable UUID uuid,
            @Parameter(description = "Delete secret from vaults as well") @RequestParam(
                    defaultValue = "false") boolean deleteInVaults)
            throws NotFoundException, ConnectorException, AttributeException;

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
    @ApiResponses(
            value = {@ApiResponse(responseCode = "204", description = "Vault profile added to secret successfully")})
    @PatchMapping(path = "/secrets/{uuid}/syncVaultProfiles/{vaultProfileUuid}",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void addVaultProfileToSecret(@Parameter(description = "UUID of the secret") @PathVariable UUID uuid,
            @Parameter(description = "UUID of the vault profile") @PathVariable UUID vaultProfileUuid,
            @RequestBody List<RequestAttribute> createSecretAttributes)
            throws NotFoundException, ConnectorException, AttributeException;

    @Operation(summary = "Remove vault profile from secret")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vault profile removed from secret successfully")})
    @DeleteMapping(path = "/secrets/{uuid}/syncVaultProfiles/{vaultProfileUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeVaultProfileFromSecret(@Parameter(description = "UUID of the secret") @PathVariable UUID uuid,
            @Parameter(description = "UUID of the vault profile") @PathVariable UUID vaultProfileUuid,
            @Parameter(description = "Delete secret in the associated vault as well") @RequestParam(
                    defaultValue = "false") boolean deleteInVault)
            throws NotFoundException, ConnectorException, AttributeException;

    @Operation(summary = "Update Secret Objects")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Secret objects updated")})
    @PatchMapping(path = "/secrets/{uuid}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateSecretObjects(@Parameter(description = "Secret UUID") @PathVariable UUID uuid,
            @RequestBody SecretUpdateObjectsDto request)
            throws NotFoundException, ConnectorException, AttributeException;

}
