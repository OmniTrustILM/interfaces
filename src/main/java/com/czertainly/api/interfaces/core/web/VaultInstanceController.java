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
import com.czertainly.api.model.core.search.SearchFieldDataByGroupDto;
import com.czertainly.api.model.core.vault.*;
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

import java.util.List;
import java.util.UUID;

@RequestMapping("/v1/vaults")
@Tag(name = "Vault Instance Management", description = "Vault Instance Management API")
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
public interface VaultInstanceController extends AuthProtectedController {

    @Operation(summary = "List Vault Instance Attributes")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Attribute information retrieved")})
    @GetMapping(path = "/{connectorUuid}/attributes", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BaseAttribute> listVaultInstanceAttributes(@Parameter(description = "Connector UUID") @PathVariable UUID connectorUuid) throws ConnectorException, NotFoundException, AttributeException;

    @Operation(summary = "List Vault Profile Attributes")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Vault profile attributes retrieved")})
    @GetMapping(path = "/{uuid}/vaultProfiles/attributes", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BaseAttribute> listVaultProfileAttributes(@Parameter(description = "Vault instance UUID") @PathVariable UUID uuid) throws ConnectorException, NotFoundException, AttributeException;

    @Operation(summary = "Details of a Vault instance")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Vault instance details retrieved")})
    @GetMapping(path = "/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    VaultInstanceDetailDto getVaultInstanceDetails(@Parameter(description = "Vault instance UUID") @PathVariable UUID uuid) throws ConnectorException, NotFoundException, AttributeException;

    @Operation(summary = "List Vault instances")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of Vault instances retrieved")})
    @PostMapping(path = "/list", produces = {MediaType.APPLICATION_JSON_VALUE})
    PaginationResponseDto<VaultInstanceDto> listVaultInstances(@RequestBody SearchRequestDto searchRequest);

    @Operation(summary = "Delete a Vault instance")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Vault instance deleted")})
    @DeleteMapping(path = "/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteVaultInstance(@Parameter(description = "Vault instance UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(summary = "Create a Vault instance")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Vault instance created")})
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    VaultInstanceDetailDto createVaultInstance(@RequestBody @Valid VaultInstanceRequestDto vaultInstanceRequest) throws ConnectorException, NotFoundException, AttributeException, AlreadyExistException;

    @Operation(summary = "Update a Vault instance")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Vault instance updated")})
    @PutMapping(path = "/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    VaultInstanceDetailDto updateVaultInstance(@Parameter(description = "Vault instance UUID") @PathVariable UUID uuid, @RequestBody VaultInstanceUpdateRequestDto vaultInstanceRequest) throws ConnectorException, NotFoundException, AttributeException;

    @Operation(operationId = "getVaultInstanceSearchableFields", summary = "List search filters for Vault instances")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of search filters retrieved")})
    @GetMapping(path = "/search", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<SearchFieldDataByGroupDto> getSearchableFieldInformation();

}
