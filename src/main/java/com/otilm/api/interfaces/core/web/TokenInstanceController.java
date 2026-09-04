package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.AttributeException;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.cryptography.token.TokenInstanceRequestDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.core.cryptography.token.TokenInstanceDetailDto;
import com.otilm.api.model.core.cryptography.token.TokenInstanceDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
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

@RequestMapping("/v1/tokens")
@Tag(name = "Token Instance Management", description = "Token Instance Management API")
public interface TokenInstanceController extends AuthProtectedController {

    // Token Instance Operation APIs
    @Operation(summary = "List Token Instances")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Token Instances retrieved")})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    List<TokenInstanceDto> listTokenInstances();

    @ApiResponses(value = {
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "200", description = "Attribute information retrieved"),
            @ApiResponse(responseCode = "404", description = "Connector not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @Operation(summary = "List available token attributes for the specified connector")
    @GetMapping(path = "/{connectorUuid}/attributes", produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listTokenAttributes(@Parameter(description = "Connector UUID") @PathVariable UUID connectorUuid,
            @Parameter(description = "Connector kind. Required for connectors based on version 1 of the connector "
                    + "framework; omit for connectors based on version 2 (NG) of the connector framework.") @Nullable @RequestParam(
                            required = false) String kind)
            throws ConnectorException, NotFoundException;

    @Operation(summary = "Get Token Instance Detail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token Instance Detail retrieved"),
            @ApiResponse(responseCode = "404", description = "Token Instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
    @GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    TokenInstanceDetailDto getTokenInstance(
            @Parameter(description = "UUID of the Token Instance") @PathVariable String uuid)
            throws ConnectorException, NotFoundException;

    @Operation(summary = "Create a new Token Instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),

            @ApiResponse(responseCode = "201", description = "Token Instance Created Successfully"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),
            @ApiResponse(responseCode = "404", description = "Token Instance, Connector or Credential not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    TokenInstanceDetailDto createTokenInstance(@RequestBody TokenInstanceRequestDto request)
            throws AlreadyExistException, ValidationException, ConnectorException, AttributeException,
            NotFoundException;

    @Operation(summary = "Update Token Instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),

            @ApiResponse(responseCode = "200", description = "Token Instance Updated"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),
            @ApiResponse(responseCode = "404", description = "Token Instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PutMapping(path = "/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    TokenInstanceDetailDto updateTokenInstance(
            @Parameter(description = "Token Instance UUID") @PathVariable String uuid,
            @RequestBody TokenInstanceRequestDto request)
            throws ConnectorException, ValidationException, AttributeException, NotFoundException;

    @Operation(summary = "Delete Token Instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Token Instance deleted"),
            @ApiResponse(responseCode = "404", description = "Token Instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @DeleteMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteTokenInstance(@Parameter(description = "Token Instance UUID") @PathVariable String uuid)
            throws NotFoundException;

    @Operation(summary = "Activate Token Instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),

            @ApiResponse(responseCode = "204", description = "Token Instance Activated"),
            @ApiResponse(responseCode = "404", description = "Token Instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PatchMapping(path = "/{uuid}/activate", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void activateTokenInstance(@Parameter(description = "Token Instance UUID") @PathVariable String uuid,
            @RequestBody List<RequestAttribute> attributes) throws ConnectorException, NotFoundException;

    @Operation(summary = "Deactivate Token Instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),

            @ApiResponse(responseCode = "204", description = "Token Instance Deactivated"),
            @ApiResponse(responseCode = "404", description = "Token Instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PatchMapping(path = "/{uuid}/deactivate", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deactivateTokenInstance(@Parameter(description = "Token Instance UUID") @PathVariable String uuid)
            throws ConnectorException, NotFoundException;

    @Operation(summary = "Delete multiple Token Instances")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Token Instances deleted"),
            @ApiResponse(responseCode = "404", description = "Token Instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @DeleteMapping(path = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteTokenInstance(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Token Instance UUIDs",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {
                    @ExampleObject(
                            value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<String> uuids);

    @Operation(summary = "Reload Token Instance status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),

            @ApiResponse(responseCode = "200", description = "Token Instance Status Reloaded from Connector"),
            @ApiResponse(responseCode = "404", description = "Token Instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PatchMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    TokenInstanceDetailDto reloadStatus(
            @Parameter(description = "UUID of the Token Instance") @PathVariable String uuid)
            throws ConnectorException, NotFoundException;

    // Token Instance related Attribute APIs

    @Operation(summary = "List Token activation Attributes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),

            @ApiResponse(responseCode = "200", description = "Token activation Attributes retrieved"),
            @ApiResponse(responseCode = "404", description = "Token Instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/{uuid}/activate/attributes", produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listTokenInstanceActivationAttributes(
            @Parameter(description = "Token Instance UUID") @PathVariable String uuid)
            throws ConnectorException, NotFoundException;
}
