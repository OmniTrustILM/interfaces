package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.*;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.authority.AuthorityInstanceRequestDto;
import com.otilm.api.model.client.authority.AuthorityInstanceUpdateRequestDto;
import com.otilm.api.model.common.*;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.core.authority.AuthorityInstanceDto;
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

@RequestMapping("/v1/authorities")
@Tag(name = "Authority Management", description = "Authority Management API")
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
public interface AuthorityInstanceController extends AuthProtectedController {

    @Operation(summary = "List of available Authority instances")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of Authority instances")})
    @GetMapping(produces = {"application/json"})
    List<AuthorityInstanceDto> listAuthorityInstances();

    @Operation(summary = "Details of an Authority instance")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Authority instance details retrieved")})
    @GetMapping(path = "/{uuid}", produces = {"application/json"})
    AuthorityInstanceDto getAuthorityInstance(@Parameter(description = "Authority instance UUID") @PathVariable String uuid) throws ConnectorException, NotFoundException;

    @Operation(summary = "Add Authority instance")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "New Authority instance added", content = @Content(schema = @Schema(implementation = UuidDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<?> createAuthorityInstance(@RequestBody AuthorityInstanceRequestDto request)
            throws AlreadyExistException, ConnectorException, AttributeException, NotFoundException;

    @Operation(summary = "Edit Authority instance")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Authority instance details updated"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PutMapping(path = "/{uuid}", consumes = {"application/json"}, produces = {
            "application/json"})
    AuthorityInstanceDto editAuthorityInstance(@Parameter(description = "Authority instance UUID") @PathVariable String uuid, @RequestBody AuthorityInstanceUpdateRequestDto request)
            throws ConnectorException, AttributeException, NotFoundException;

    @Operation(summary = "Delete Authority instance")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Authority instance deleted")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{uuid}", produces = {"application/json"})
    void deleteAuthorityInstance(@Parameter(description = "Authority instance UUID") @PathVariable String uuid) throws ConnectorException, NotFoundException;

    @Operation(summary = "List entity profiles")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Entity profiles retrieved")})
    @GetMapping(path = "/{uuid}/endentityprofiles", produces = {"application/json"})
    List<NameAndIdDto> listEntityProfiles(@Parameter(description = "Authority instance UUID") @PathVariable String uuid) throws ConnectorException, NotFoundException;

    @Operation(summary = "List certificate profiles")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Certificate profiles retrieved")})
    @GetMapping(path = "/{uuid}/endentityprofiles/{endEntityProfileId}/certificateprofiles", produces = {"application/json"})
    List<NameAndIdDto> listCertificateProfiles(@Parameter(description = "Authority instance UUID") @PathVariable String uuid, @PathVariable Integer endEntityProfileId)
            throws ConnectorException, NotFoundException;

    @Operation(summary = "List CAs in Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "CAs in Profile retrieved")})
    @GetMapping(path = "/{uuid}/endentityprofiles/{endEntityProfileId}/cas", produces = {"application/json"})
    List<NameAndIdDto> listCAsInProfile(@Parameter(description = "Authority instance UUID") @PathVariable String uuid, @PathVariable Integer endEntityProfileId)
            throws ConnectorException, NotFoundException;

    @Operation(summary = "List RA Profile Attributes")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Attribute information retrieved")})
    @GetMapping(path = "/{uuid}/attributes/raProfile", produces = {"application/json"})
    List<BaseAttribute> listRAProfileAttributes(@Parameter(description = "Authority instance UUID") @PathVariable String uuid) throws ConnectorException, NotFoundException;

    @Operation(summary = "Validate RA Profile Attributes")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Attribute information validated")})
    @PostMapping(path = "/{uuid}/attributes/raProfile/validate", consumes = {
            "application/json"}, produces = {"application/json"})
    void validateRAProfileAttributes(@Parameter(description = "Authority instance UUID") @PathVariable String uuid, @RequestBody List<RequestAttribute>attributes)
            throws ConnectorException, NotFoundException;

    @Operation(summary = "Delete multiple Authority instances")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Authority instances deleted"),
            @ApiResponse(responseCode = "422", description = "Unprocessible Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @DeleteMapping(produces = {"application/json"})
    List<BulkActionMessageDto> bulkDeleteAuthorityInstance(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Authority Instance UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")}))
                                                           @RequestBody List<String> uuids) throws ConnectorException, ValidationException;

    @Operation(summary = "Force delete multiple Authority instances")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Authority instances forced to delete"),
            @ApiResponse(responseCode = "422", description = "Unprocessible Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @DeleteMapping(path = "/force", produces = {"application/json"})
    List<BulkActionMessageDto> forceDeleteAuthorityInstances(@RequestBody List<String> uuids) throws NotFoundException, ValidationException;

}
