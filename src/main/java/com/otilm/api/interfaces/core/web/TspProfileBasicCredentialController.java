package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.signing.protocols.tsp.TspBasicCredentialDto;
import com.otilm.api.model.client.signing.protocols.tsp.TspBasicCredentialRequestDto;
import com.otilm.api.model.common.ErrorMessageDto;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.UUID;

@RequestMapping("/v1/tspProfiles/{tspProfileUuid}/basicCredentials")
@Tag(name = "TSP Profile Basic Credential Management", description = "APIs for managing Basic credentials of a TSP Profile")
@ApiResponses(value = {@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
public interface TspProfileBasicCredentialController extends AuthProtectedController {

    @Operation(operationId = "listTspProfileBasicCredentials", summary = "List Basic credentials of a TSP Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Basic credentials retrieved")})
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    List<TspBasicCredentialDto> list(@Parameter(description = "TSP Profile UUID") @PathVariable UUID tspProfileUuid) throws NotFoundException;

    @Operation(operationId = "getTspProfileBasicCredential", summary = "Get a Basic credential")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Basic credential retrieved")})
    @GetMapping(path = "/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    TspBasicCredentialDto get(@Parameter(description = "TSP Profile UUID") @PathVariable UUID tspProfileUuid,
                              @Parameter(description = "Basic credential UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "createTspProfileBasicCredential", summary = "Create a Basic credential")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Basic credential created"),
            @ApiResponse(responseCode = "409", description = "Already Exists", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
    })
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    TspBasicCredentialDto create(@Parameter(description = "TSP Profile UUID") @PathVariable UUID tspProfileUuid,
                                 @RequestBody @Valid TspBasicCredentialRequestDto request) throws NotFoundException;

    @Operation(operationId = "updateTspProfileBasicCredential", summary = "Update a Basic credential")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Basic credential updated"),
            @ApiResponse(responseCode = "409", description = "Already Exists", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
    })
    @PutMapping(path = "/{uuid}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    TspBasicCredentialDto update(@Parameter(description = "TSP Profile UUID") @PathVariable UUID tspProfileUuid,
                                 @Parameter(description = "Basic credential UUID") @PathVariable UUID uuid,
                                 @RequestBody @Valid TspBasicCredentialRequestDto request) throws NotFoundException;

    @Operation(operationId = "deleteTspProfileBasicCredential", summary = "Delete a Basic credential")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Basic credential deleted")})
    @DeleteMapping(path = "/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@Parameter(description = "TSP Profile UUID") @PathVariable UUID tspProfileUuid,
                @Parameter(description = "Basic credential UUID") @PathVariable UUID uuid) throws NotFoundException;
}
