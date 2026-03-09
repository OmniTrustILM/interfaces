package com.czertainly.api.interfaces.core.web;

import com.czertainly.api.exception.AlreadyExistException;
import com.czertainly.api.exception.AttributeException;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.NotFoundException;
import com.czertainly.api.interfaces.AuthProtectedController;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.client.certificate.EntityInstanceResponseDto;
import com.czertainly.api.model.client.certificate.SearchRequestDto;
import com.czertainly.api.model.client.entity.EntityInstanceRequestDto;
import com.czertainly.api.model.client.entity.EntityInstanceUpdateRequestDto;
import com.czertainly.api.model.common.ErrorMessageDto;
import com.czertainly.api.model.common.UuidDto;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.core.entity.EntityInstanceDto;
import com.czertainly.api.model.core.search.SearchFieldDataByGroupDto;
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

@RequestMapping("/v1/entities")
@Tag(name = "Entity Management", description = "Entity Management API")
@ApiResponses(
        value = {
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
public interface EntityInstanceController extends AuthProtectedController {

    @Operation(
            summary = "List Entity instances"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of Entity instances"
                    )
            })
    @PostMapping(
            path = "/list",
            produces = {"application/json"}
    )
    EntityInstanceResponseDto listEntityInstances(@RequestBody SearchRequestDto request);

    @Operation(operationId = "getEntitySearchableFields", summary = "Get Entities searchable fields information")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Entity searchable field information retrieved")})
    @GetMapping(path = "/search", produces = {"application/json"})
    List<SearchFieldDataByGroupDto> getSearchableFieldInformation();

    @Operation(
            summary = "Get Entity instance details"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Authority instance details retrieved"
                    ),
                    @ApiResponse(responseCode = "404", description = "Entity not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @GetMapping(
            path = "/{entityUuid}",
            produces = {"application/json"}
    )
    EntityInstanceDto getEntityInstance(
            @Parameter(description = "Entity instance UUID") @PathVariable String entityUuid
    ) throws ConnectorException, NotFoundException;

    @Operation(
            summary = "Add Entity instance"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Entity instance created",
                            content = @Content(schema = @Schema(implementation = UuidDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})
                    ),
                    @ApiResponse(responseCode = "404", description = "Connector not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @PostMapping(
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    ResponseEntity<?> createEntityInstance(
            @RequestBody EntityInstanceRequestDto request
    ) throws AlreadyExistException, ConnectorException, AttributeException, NotFoundException;

    @Operation(
            summary = "Edit Entity instance"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Authority instance details updated"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})
                    ),
                    @ApiResponse(responseCode = "404", description = "Entity or connector not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @PutMapping(
            path = "/{entityUuid}",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    EntityInstanceDto editEntityInstance(
            @Parameter(description = "Entity instance UUID") @PathVariable String entityUuid,
            @RequestBody EntityInstanceUpdateRequestDto request
    ) throws ConnectorException, AttributeException, NotFoundException;

    @Operation(
            summary = "Delete Entity instance"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Entity instance deleted"
                    ),
                    @ApiResponse(responseCode = "404", description = "Entity not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @DeleteMapping(path = "/{entityUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteEntityInstance(
            @Parameter(description = "Entity instance UUID") @PathVariable String entityUuid
    ) throws ConnectorException, NotFoundException;

    @Operation(
            summary = "List Location Attributes"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Location attributes retrieved"
                    ),
                    @ApiResponse(responseCode = "404", description = "Entity not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @GetMapping(
            path = "/{entityUuid}/attributes/location",
            produces = {"application/json"}
    )
    List<BaseAttribute> listLocationAttributes(
            @Parameter(description = "Entity instance UUID") @PathVariable String entityUuid
    ) throws ConnectorException, NotFoundException;

    @Operation(
            summary = "Validate Location Attributes"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Attributes validated"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})
                    ),
                    @ApiResponse(responseCode = "404", description = "Entity not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @PostMapping(
            path = "/{entityUuid}/attributes/location/validate",
            consumes = {"application/json"}
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void validateLocationAttributes(
            @Parameter(description = "Entity instance UUID") @PathVariable String entityUuid,
            @RequestBody List<RequestAttribute> attributes
    ) throws ConnectorException, NotFoundException;
}
