package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.AttributeException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.auth.RoleRequestDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.core.auth.*;
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

@RequestMapping("/v1/roles")
@Tag(name = "Role Management", description = "Role Management API")
public interface RoleManagementController extends AuthProtectedController {

    @Operation(summary = "List Roles")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of roles fetched")})
    @GetMapping(produces = {"application/json"})
    List<RoleDto> listRoles();

    @Operation(summary = "Get role details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role details retrieved"),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping(path = "/{roleUuid}", produces = {"application/json"})
    RoleDetailDto getRole(@Parameter(description = "Role UUID") @PathVariable String roleUuid) throws NotFoundException;

    @Operation(summary = "Create Role")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Role created")})
    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<RoleDetailDto> createRole(@RequestBody RoleRequestDto request) throws NotFoundException, AttributeException;

    @Operation(summary = "Update Role")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Role details updated")})
    @PutMapping(path = "/{roleUuid}", consumes = {"application/json"}, produces = {"application/json"})
    RoleDetailDto updateRole(@Parameter(description = "Role UUID") @PathVariable String roleUuid, @RequestBody RoleRequestDto request) throws NotFoundException, AttributeException;

    @Operation(summary = "Delete Role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Role deleted"),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @DeleteMapping(path = "/{roleUuid}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteRole(@Parameter(description = "Role UUID") @PathVariable String roleUuid) throws NotFoundException;

    @Operation(summary = "Get Role Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role users retrieved"),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping(path = "/{roleUuid}/users", produces = {"application/json"})
    List<UserDto> getRoleUsers(@Parameter(description = "Role UUID") @PathVariable String roleUuid) throws NotFoundException;

    @Operation(summary = "Add users to Role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role users updated"),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PatchMapping(path = "/{roleUuid}/users", consumes = {"application/json"}, produces = {"application/json"})
    RoleDetailDto updateUsers(@Parameter(description = "Role UUID") @PathVariable String roleUuid, @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")}))
    @RequestBody List<String> userUuids) throws NotFoundException;

    @Operation(summary = "Get Permissions of a Role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role permissions retrieved"),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping(path = "/{roleUuid}/permissions", produces = {"application/json"})
    SubjectPermissionsDto getRolePermissions(@Parameter(description = "Role UUID") @PathVariable String roleUuid) throws NotFoundException;

    @Operation(summary = "Add permissions to Role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User roles updated"),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PostMapping(path = "/{roleUuid}/permissions", consumes = {"application/json"}, produces = {"application/json"})
    SubjectPermissionsDto savePermissions(@Parameter(description = "Role UUID") @PathVariable String roleUuid, @RequestBody RolePermissionsRequestDto request) throws NotFoundException;

    @Operation(summary = "Get Resources of a Role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role resources retrieved"),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping(path = "/{roleUuid}/permissions/{resourceUuid}", produces = {"application/json"})
    ResourcePermissionsDto getRoleResourcePermissions(@Parameter(description = "Role UUID") @PathVariable String roleUuid,
                                                      @Parameter(description = "Resource UUID") @PathVariable String resourceUuid) throws NotFoundException;

    @Operation(summary = "Get Resource Objects of a Role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource Objects retrieved"),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping(path = "/{roleUuid}/permissions/{resourceUuid}/objects", produces = {"application/json"})
    List<ObjectPermissionsDto> getResourcePermissionObjects(@Parameter(description = "Role UUID") @PathVariable String roleUuid,
                                                            @Parameter(description = "Resource UUID") @PathVariable String resourceUuid) throws NotFoundException;

    @Operation(summary = "Add Resource Objects to a Role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Resource Objects added"),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PostMapping(path = "/{roleUuid}/permissions/{resourceUuid}/objects", consumes = {"application/json"}, produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void addResourcePermissionObjects(@Parameter(description = "Role UUID") @PathVariable String roleUuid,
                                      @Parameter(description = "Resource UUID") @PathVariable String resourceUuid,
                                      @RequestBody List<ObjectPermissionsRequestDto> request) throws NotFoundException;

    @Operation(summary = "Update Resource Objects to a Role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Resource Objects added"),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PutMapping(path = "/{roleUuid}/permissions/{resourceUuid}/objects/{objectUuid}", consumes = {"application/json"}, produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateResourcePermissionObjects(@Parameter(description = "Role UUID") @PathVariable String roleUuid,
                                         @Parameter(description = "Resource UUID") @PathVariable String resourceUuid,
                                         @Parameter(description = "Object UUID") @PathVariable String objectUuid,
                                         @RequestBody ObjectPermissionsRequestDto request) throws NotFoundException;

    @Operation(summary = "Update Resource Objects to a Role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Resource Objects added"),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @DeleteMapping(path = "/{roleUuid}/permissions/{resourceUuid}/objects/{objectUuid}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeResourcePermissionObjects(@Parameter(description = "Role UUID") @PathVariable String roleUuid,
                                         @Parameter(description = "Resource UUID") @PathVariable String resourceUuid,
                                         @Parameter(description = "Object UUID") @PathVariable String objectUuid) throws NotFoundException;
}
