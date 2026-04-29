package com.otilm.api.interfaces.core.web.v2;

import com.otilm.api.exception.*;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.certificate.SearchRequestDto;
import com.otilm.api.model.client.connector.ConnectRequestDto;
import com.otilm.api.model.client.connector.v2.ConnectorInfo;
import com.otilm.api.model.client.connector.v2.HealthInfo;
import com.otilm.api.model.common.BulkActionMessageDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.common.PaginationResponseDto;
import com.otilm.api.model.core.connector.v2.*;
import com.otilm.api.model.core.search.SearchFieldDataByGroupDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.net.ConnectException;
import java.util.List;
import java.util.UUID;

@RequestMapping("/v2/connectors")
@Tag(name = "Connector Management v2", description = "Connector Management v2 API")
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
public interface ConnectorController extends AuthProtectedController {

    @Operation(operationId = "listConnectorsV2", summary = "List Connectors")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List all Connectors")})
    @PostMapping(path = "/list", produces = {"application/json"})
    PaginationResponseDto<ConnectorDto> listConnectors(@RequestBody SearchRequestDto request)
            throws NotFoundException;

    @Operation(operationId = "getConnectorSearchableFields", summary = "Get Connectors searchable fields information")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Connector searchable field information retrieved")})
    @GetMapping(path = "/search", produces = {"application/json"})
    List<SearchFieldDataByGroupDto> getSearchableFieldInformation();

    @Operation(operationId = "getConnectorV2", summary = "Get details of a Connector")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Connector details retrieved")})
    @GetMapping(path = "/{uuid}", produces = {"application/json"})
    ConnectorDetailDto getConnector(@Parameter(description = "Connector UUID") @PathVariable UUID uuid) throws NotFoundException, ConnectorException;

    @Operation(operationId = "createConnectorV2", summary = "Create a new Connector")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New Connector created"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    ConnectorDetailDto createConnector(@RequestBody @Valid ConnectorRequestDto request)
            throws AlreadyExistException, ConnectorException, AttributeException, NotFoundException;

    @Operation(operationId = "editConnectorV2", summary = "Edit a Connector")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connector updated"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PutMapping(path = "/{uuid}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {"application/json"})
    ConnectorDetailDto editConnector(@Parameter(description = "Connector UUID") @PathVariable UUID uuid, @RequestBody @Valid ConnectorUpdateRequestDto request)
            throws ConnectorException, AttributeException, NotFoundException;

    @Operation(operationId = "deleteConnectorV2", summary = "Delete a Connector")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Connector deleted"),
            @ApiResponse(responseCode = "404", description = "Connector not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{uuid}", produces = {"application/json"})
    void deleteConnector(@Parameter(description = "Connector UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "connectV2", summary = "Connect to a Connector")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Connector connected"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(path = "/connect", consumes = {"application/json"}, produces = {
            "application/json"})
    List<ConnectInfo> connect(@RequestBody @Valid ConnectRequestDto request) throws ValidationException, ConnectException, ConnectorException;

    @Operation(operationId = "reconnectV2", summary = "Reconnect Connector")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Reconnect to a Connector"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(path = "/{uuid}/reconnect", produces = {"application/json"})
    ConnectInfo reconnect(@Parameter(description = "Connector UUID") @PathVariable UUID uuid) throws ValidationException, NotFoundException, ConnectException, ConnectorException;

    @Operation(operationId = "approveV2", summary = "Approve a Connector")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Connector Approved")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(path = "/{uuid}/approve", produces = {"application/json"})
    void approve(@Parameter(description = "Connector UUID") @PathVariable UUID uuid) throws NotFoundException, ValidationException;

    @Operation(operationId = "bulkApproveV2", summary = "Approve multiple Connector")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Approve multiple Connectors")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(path = "/approve", consumes = {"application/json"}, produces = {"application/json"})
    List<BulkActionMessageDto> bulkApprove(@RequestBody List<UUID> uuids) throws NotFoundException, ValidationException;

    @Operation(operationId = "bulkReconnectV2", summary = "Reconnect multiple Connectors")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Reconnect multiple Connectors initiated")})
    @PostMapping(path = "/reconnect", consumes = {
            "application/json"}, produces = {"application/json"})
    List<BulkActionMessageDto> bulkReconnect(@RequestBody List<UUID> uuids) throws ValidationException, NotFoundException, ConnectException, ConnectorException;

    @Operation(operationId = "bulkDeleteConnectorV2", summary = "Delete multiple Connectors")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Connectors deleted"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @DeleteMapping(produces = {"application/json"})
    List<BulkActionMessageDto> bulkDeleteConnector(@RequestBody List<UUID> uuids) throws NotFoundException, ValidationException, ConnectorException;

    @Operation(operationId = "bulkForceDeleteConnectorV2", summary = "Force Delete multiple Connectors")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Connectors deleted"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(path = "/forceDelete", produces = {"application/json"})
    List<BulkActionMessageDto> bulkForceDeleteConnector(@RequestBody List<UUID> uuids) throws NotFoundException, ValidationException;

    @Operation(operationId = "checkHealthV2", summary = "Check Health of a Connector")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Health check completed")})
    @GetMapping(path = "/{uuid}/health", produces = {MediaType.APPLICATION_JSON_VALUE})
    HealthInfo checkHealth(@Parameter(description = "Connector UUID") @PathVariable UUID uuid) throws NotFoundException, ConnectorException;

    @Operation(operationId = "getInfoV2", summary = "Get Info about Connector")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Connector info retrieved")})
    @GetMapping(path = "/{uuid}/info", produces = {MediaType.APPLICATION_JSON_VALUE})
    ConnectorInfo getInfo(@Parameter(description = "Connector UUID") @PathVariable UUID uuid) throws NotFoundException, ConnectorException;

}
