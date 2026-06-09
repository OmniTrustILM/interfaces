package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.AttributeException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.attribute.AttributeDefinitionDto;
import com.otilm.api.model.client.attribute.metadata.*;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.common.UuidDto;
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
import java.util.Optional;

@RequestMapping("/v1/attributes/metadata")
@Tag(name = "Global Metadata", description = "Global Metadata API")
public interface GlobalMetadataController extends AuthProtectedController {
    @Operation(summary = "List Global Metadata")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "list of available Global Metadata")})
    @GetMapping(produces = {"application/json"})
    List<AttributeDefinitionDto> listGlobalMetadata();

    @Operation(summary = "Global Metadata details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Global Metadata details retrieved"),
            @ApiResponse(responseCode = "404", description = "Global Metadata not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping(path = "/{uuid}", produces = {"application/json"})
    GlobalMetadataDefinitionDetailDto getGlobalMetadata(@PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Create Global Metadata")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Global Metadata created", content = @Content(schema = @Schema(implementation = UuidDto.class)))})
    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<GlobalMetadataDefinitionDetailDto> createGlobalMetadata(@RequestBody GlobalMetadataCreateRequestDto request)
            throws AlreadyExistException, AttributeException;

    @Operation(summary = "Edit Global Metadata")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Global Metadata updated"),
            @ApiResponse(responseCode = "404", description = "Global Metadata not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PutMapping(path = "/{uuid}", consumes = {"application/json"}, produces = {"application/json"})
    GlobalMetadataDefinitionDetailDto editGlobalMetadata(@Parameter(description = "Global Metadata UUID") @PathVariable String uuid, @RequestBody GlobalMetadataUpdateRequestDto request)
            throws NotFoundException, AttributeException;

    @Operation(summary = "Delete Global Metadata")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Global Metadata deleted"),
            @ApiResponse(responseCode = "404", description = "Global Metadata not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @DeleteMapping(path = "/{uuid}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteGlobalMetadata(@Parameter(description = "Global Metadata UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Delete multiple Global Metadata")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Global Metadata deleted")})
    @DeleteMapping(produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void bulkDeleteGlobalMetadata(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Global Metadata UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")}))
                                  @RequestBody List<String> attributeUuids);

    @Operation(summary = "Get Available Connector Metadata")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Connector Metadata retrieved")})
    @GetMapping(path = "/promote", produces = {"application/json"})
    List<ConnectorMetadataResponseDto> getConnectorMetadata(@RequestParam Optional<String> connectorUuid);

    @Operation(summary = "Promote Connector Metadata to Global Metadata")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Connector Metadata promoted to global metadata"),
            @ApiResponse(responseCode = "404", description = "Connector not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PostMapping(path = "/promote",consumes = {"application/json"}, produces = {"application/json"})
    GlobalMetadataDefinitionDetailDto promoteConnectorMetadata(@RequestBody ConnectorMetadataPromotionRequestDto request) throws NotFoundException;
}
