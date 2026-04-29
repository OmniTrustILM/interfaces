package com.otilm.api.interfaces.connector.entity;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.interfaces.AuthProtectedConnectorController;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.entity.EntityInstanceDto;
import com.otilm.api.model.connector.entity.EntityInstanceRequestDto;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/entityProvider/entities")
@Tag(
        name = "Entity Management",
        description = "Management interfaces to control Entities in the platform. " +
                "Entities can be created, edited, removed. Support for the bulk operation and listing of available " +
                "Entities for the automation. Location attributes and validation."
)
public interface EntityController extends AuthProtectedConnectorController {

    @Operation(
            summary = "List Entity instances",
            description = "List available Entity instances"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Entity instances retrieved"
                    )
            })
    @GetMapping(
            produces = {"application/json"}
    )
    List<EntityInstanceDto> listEntityInstances();

    @Operation(
            summary = "Get Entity instance details"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Entity instance retrieved"
                    )
            })
    @GetMapping(
            path = "/{entityUuid}",
            produces = {"application/json"}
    )
    EntityInstanceDto getEntityInstance(@Parameter(description = "Entity instance UUID") @PathVariable String entityUuid) throws NotFoundException;

    @Operation(
            summary = "Create Entity instance"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Entity instance created"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Attribute validation failed",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class, examples = {"Attribute validation error message"})),
                                    examples={@ExampleObject(value="[\"Error Message 1\",\"Error Message 2\"]")})

                    )
            })
    @PostMapping(
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    EntityInstanceDto createEntityInstance(@RequestBody EntityInstanceRequestDto request) throws AlreadyExistException;

    @Operation(
            summary = "Update Entity instance"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Entity instance updated"
                    )
            })
    @PutMapping(
            path = "/{entityUuid}",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    EntityInstanceDto updateEntityInstance(@Parameter(description = "Entity instance UUID") @PathVariable String entityUuid, @RequestBody EntityInstanceRequestDto request) throws NotFoundException;

    @Operation(
            summary = "Remove Entity instance"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Entity instance removed"
                    )
            })
    @DeleteMapping(
            path = "/{entityUuid}"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeEntityInstance(@Parameter(description = "Entity instance UUID") @PathVariable String entityUuid) throws NotFoundException;

    @Operation(
            summary = "List Entity Location Attributes"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Entity Location Attributes retrieved"
                    )
            })
    @GetMapping(
            path = "/{entityUuid}/location/attributes",
            produces = {"application/json"}
    )
    List<BaseAttribute> listLocationAttributes(
            @Parameter(description = "Entity instance UUID") @PathVariable String entityUuid) throws NotFoundException;

    @Operation(
            summary = "Validate Location Attributes"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Entity Location Attributes validation completed"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Attribute validation failed",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class, examples = {"Attribute validation error message"})),
                                    examples={@ExampleObject(value="[\"Error Message 1\",\"Error Message 2\"]")})

                    )
            })
    @PostMapping(
            path = "/{entityUuid}/location/attributes/validate",
            consumes = {"application/json"}
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void validateLocationAttributes(
            @Parameter(description = "Entity instance UUID") @PathVariable String entityUuid,
            @RequestBody List<RequestAttribute>attributes) throws ValidationException, NotFoundException;
}
