package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.*;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.connector.ConnectDto;
import com.otilm.api.model.client.connector.ConnectRequestDto;
import com.otilm.api.model.client.connector.ConnectorRequestDto;
import com.otilm.api.model.client.connector.ConnectorUpdateRequestDto;
import com.otilm.api.model.common.*;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.model.core.connector.ConnectorStatus;
import com.otilm.api.model.core.connector.FunctionGroupCode;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.ConnectException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/v1/connectors")
@Tag(name = "Connector Management", description = "Connector Management API")
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

	@Operation(summary = "List Connectors by Function Group and Kind")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "List all Connectors")})
	@GetMapping(produces = {"application/json"})
    List<ConnectorDto> listConnectors(@RequestParam Optional<FunctionGroupCode> functionGroup, @RequestParam Optional<String> kind, @RequestParam Optional<ConnectorStatus> status)
			throws NotFoundException;

	@Operation(summary = "Get details of a Connector")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Connector details retrieved")})
	@GetMapping(path = "/{uuid}", produces = {"application/json"})
    ConnectorDto getConnector(@Parameter(description = "Connector UUID") @PathVariable String uuid) throws NotFoundException, ConnectorException;

	@Operation(summary = "Create a new Connector")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "New Connector created", content = @Content(schema = @Schema(implementation = UuidDto.class))),
			@ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples={@ExampleObject(value="[\"Error Message 1\",\"Error Message 2\"]")})),})

	@PostMapping(consumes = { "application/json" }, produces = { "application/json" })
    ResponseEntity<?> createConnector(@RequestBody ConnectorRequestDto request)
            throws AlreadyExistException, ConnectorException, AttributeException, NotFoundException;

	@Operation(summary = "Edit a Connector")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Connector updated"),
			@ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples={@ExampleObject(value="[\"Error Message 1\",\"Error Message 2\"]")}))})
	@PutMapping(path = "/{uuid}", consumes = { "application/json" }, produces = {
			"application/json" })
    ConnectorDto editConnector(@Parameter(description = "Connector UUID") @PathVariable String uuid, @RequestBody ConnectorUpdateRequestDto request)
            throws ConnectorException, AttributeException, NotFoundException;

	@Operation(summary = "Delete a Connector")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Connector deleted")})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping(path = "/{uuid}", produces = {"application/json"})
    void deleteConnector(@Parameter(description = "Connector UUID") @PathVariable String uuid) throws NotFoundException;

	@Operation(summary = "Connect to a Connector")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Connector connected"),
			@ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples={@ExampleObject(value="[\"Error Message 1\",\"Error Message 2\"]")}))})
	@PutMapping(path = "/connect", consumes = { "application/json" }, produces = {
			"application/json" })
    List<ConnectDto> connect(@RequestBody ConnectRequestDto request) throws ValidationException, ConnectException, ConnectorException;

	@Operation(summary = "Reconnect to a Connector")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Reconnect to a Connector"),
			@ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples={@ExampleObject(value="[\"Error Message 1\",\"Error Message 2\"]")}))})
	@PutMapping(path = "/{uuid}/reconnect", produces = { "application/json" })
    List<ConnectDto> reconnect(@Parameter(description = "Connector UUID") @PathVariable String uuid) throws ValidationException, NotFoundException, ConnectException, ConnectorException;

	@Operation(summary = "Approve multiple Connector")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Approve multiple Connectors")})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PutMapping(path = "/approve", consumes = { "application/json" }, produces = { "application/json" })
    void bulkApprove(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Connector UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")}))
                     @RequestBody List<String> uuids) throws NotFoundException, ValidationException;

	@Operation(summary = "Reconnect multiple Connectors")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Reconnect multiple Connectors initiated")})
	@PutMapping(path = "/reconnect", consumes = {
			"application/json" }, produces = { "application/json" })
    void bulkReconnect(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Connector UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")}))
                       @RequestBody List<String> uuids) throws ValidationException, NotFoundException, ConnectException, ConnectorException;

	@Operation(summary = "Approve a Connector")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Connector Approved") })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PutMapping(path = "/{uuid}/approve", produces = { "application/json" })
    void approve(@Parameter(description = "Connector UUID") @PathVariable String uuid) throws NotFoundException, ValidationException;

	@Operation(summary = "Check Health of a Connector")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Health check completed")})
	@GetMapping(path = "/{uuid}/health", produces = {"application/json"})
    HealthDto checkHealth(@Parameter(description = "Connector UUID") @PathVariable String uuid) throws NotFoundException, ValidationException, ConnectorException;

	@Operation(summary = "Get Attributes from a Connector")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Attributes received")})
	@GetMapping(path = "/{uuid}/attributes/{functionGroup}/{kind}", produces = {
			"application/json" })
    List<BaseAttribute> getAttributes(@Parameter(description = "Connector UUID") @PathVariable String uuid, @Parameter(description = "Function Group name") @PathVariable FunctionGroupCode functionGroup,
                                      @Parameter(description = "Kind") @PathVariable String kind) throws NotFoundException, ConnectorException;

	
	@Operation(summary = "Get attributes of all Function Groups")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Attributes received")})
	@GetMapping(path = "/{uuid}/attributes", produces = {"application/json"})
    Map<FunctionGroupCode, Map<String, List<BaseAttribute>>> getAttributesAll(@Parameter(description = "Connector UUID") @PathVariable String uuid) throws NotFoundException, ConnectorException;

	@Operation(summary = "Validate Attributes")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Attributes Validated")})
	@PostMapping(path = "/{uuid}/{functionGroup}/{kind}/validate", consumes = {
			"application/json" }, produces = { "application/json" })
    void validateAttributes(@Parameter(description = "Connector UUID") @PathVariable String uuid, @Parameter(description = "Function Group name") @PathVariable String functionGroup,
                            @Parameter(description = "Kind") @PathVariable String kind, @RequestBody List<RequestAttribute> attributes)
			throws NotFoundException, ConnectorException;

	@Operation(summary = "Delete multiple Connectors")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Connectors deleted"),
			@ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
	@DeleteMapping(produces = {"application/json"})
    List<BulkActionMessageDto> bulkDeleteConnector(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Connector UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")}))
                                                   @RequestBody List<String> uuids) throws NotFoundException, ValidationException, ConnectorException;

	@Operation(summary = "Force Delete multiple Connectors")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Connectors deleted"),
			@ApiResponse(responseCode = "422", description = "Unprocessible Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
	@DeleteMapping(path = "/force", produces = {"application/json"})
    List<BulkActionMessageDto> forceDeleteConnector(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Connector UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")}))
                                                    @RequestBody List<String> uuids) throws NotFoundException, ValidationException;


}
