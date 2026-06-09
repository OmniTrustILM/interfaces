package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.*;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.credential.CredentialRequestDto;
import com.otilm.api.model.client.credential.CredentialUpdateRequestDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.common.UuidDto;
import com.otilm.api.model.core.credential.CredentialDto;
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

import java.util.List;

@RequestMapping("/v1/credentials")
@Tag(name = "Credential Management", description = "Credential Management API")
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

public interface CredentialController extends AuthProtectedController {

    @Operation(summary = "List of All Credentials")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of all Credentials")})
    @GetMapping(produces = {"application/json"})
    List<CredentialDto> listCredentials();

    @Operation(summary = "Details of a Credentials")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Credential details retrieved")})
    @GetMapping(path = "/{uuid}", produces = {"application/json"})
    CredentialDto getCredential(@Parameter(description = "Credential UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Add Credential")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "New Credential added", content = @Content(schema = @Schema(implementation = UuidDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<?> createCredential(@RequestBody CredentialRequestDto request)
            throws AlreadyExistException, NotFoundException, ConnectorException, AttributeException;

    @Operation(summary = "Edit Credential")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Credential updated"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PutMapping(path = "/{uuid}", consumes = {"application/json"}, produces = {
            "application/json"})
    CredentialDto editCredential(@Parameter(description = "Credential UUID") @PathVariable String uuid, @RequestBody CredentialUpdateRequestDto request)
            throws NotFoundException, ConnectorException, AttributeException;

    @Operation(summary = "Delete Credential")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Credential deleted"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @DeleteMapping(path = "/{uuid}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCredential(@Parameter(description = "Credential UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Enable Credential")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Credential enabled")})
    @PatchMapping(path = "/{uuid}/enable", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void enableCredential(@Parameter(description = "Credential UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Disable Credential")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Credential disabled")})
    @PatchMapping(path = "/{uuid}/disable", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void disableCredential(@Parameter(description = "Credential UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Delete multiple Credentials")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Credentials deleted"),
            @ApiResponse(responseCode = "422", description = "Unprocessible Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @DeleteMapping(produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void bulkDeleteCredential(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Credential UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")}))
                                                            @RequestBody List<String> uuids) throws NotFoundException, ValidationException;
}
