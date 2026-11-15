package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.AttributeException;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.est.EstProfileEditRequestDto;
import com.otilm.api.model.client.est.EstProfileRequestDto;
import com.otilm.api.model.common.BulkActionMessageDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.core.est.EstProfileDetailDto;
import com.otilm.api.model.core.est.EstProfileDto;
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
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/v1/estProfiles")
@Tag(name = "EST Profile Management", description = "Enrollment over Secure Transport (EST) Profile Management API")
@ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Not Found",
                content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
public interface EstProfileController extends AuthProtectedController {

    @Operation(operationId = "listEstProfiles", summary = "List EST Profiles")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "EST Profiles retrieved")})
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    List<EstProfileDto> listEstProfiles();

    @Operation(operationId = "getEstProfile", summary = "Details of an EST Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "EST Profile details retrieved")})
    @GetMapping(path = "/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    EstProfileDetailDto getEstProfile(@Parameter(description = "EST Profile UUID") @PathVariable UUID uuid)
            throws NotFoundException;

    @Operation(operationId = "createEstProfile", summary = "Add new EST Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New EST Profile added"),
            @ApiResponse(responseCode = "409", description = "Already Exists",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Content",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    EstProfileDetailDto createEstProfile(@RequestBody @Valid EstProfileRequestDto request)
            throws AlreadyExistException, ConnectorException, AttributeException, NotFoundException;

    @Operation(operationId = "updateEstProfile", summary = "Update EST Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "EST Profile updated"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Content",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PutMapping(path = "/{uuid}", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    EstProfileDetailDto updateEstProfile(@Parameter(description = "EST Profile UUID") @PathVariable UUID uuid,
            @RequestBody @Valid EstProfileEditRequestDto request)
            throws ConnectorException, AttributeException, NotFoundException;

    @Operation(operationId = "deleteEstProfile", summary = "Delete EST Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "EST Profile deleted"),
            @ApiResponse(responseCode = "422", description = "EST Profile is activated on RA Profiles",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @DeleteMapping(path = "/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteEstProfile(@Parameter(description = "EST Profile UUID") @PathVariable UUID uuid)
            throws NotFoundException;

    @Operation(operationId = "bulkDeleteEstProfiles", summary = "Delete multiple EST Profiles")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Delete results per EST Profile")})
    @DeleteMapping(path = "/delete", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BulkActionMessageDto> bulkDeleteEstProfiles(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "EST Profile UUIDs",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UUID.class)), examples = {
                    @ExampleObject(
                            value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<UUID> uuids);

    @Operation(operationId = "forceDeleteEstProfiles", summary = "Force delete multiple EST Profiles",
            description = "Deactivates the EST Profiles on their RA Profiles before deleting them")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Delete results per EST Profile")})
    @DeleteMapping(path = "/delete/force", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BulkActionMessageDto> forceDeleteEstProfiles(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "EST Profile UUIDs",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UUID.class)), examples = {
                    @ExampleObject(
                            value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<UUID> uuids);

    @Operation(operationId = "enableEstProfile", summary = "Enable EST Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "EST Profile enabled")})
    @PatchMapping(path = "/{uuid}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void enableEstProfile(@Parameter(description = "EST Profile UUID") @PathVariable UUID uuid)
            throws NotFoundException;

    @Operation(operationId = "bulkEnableEstProfiles", summary = "Enable multiple EST Profiles")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "EST Profiles enabled")})
    @PatchMapping(path = "/enable", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void bulkEnableEstProfiles(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "EST Profile UUIDs",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UUID.class)), examples = {
                    @ExampleObject(
                            value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<UUID> uuids);

    @Operation(operationId = "disableEstProfile", summary = "Disable EST Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "EST Profile disabled")})
    @PatchMapping(path = "/{uuid}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void disableEstProfile(@Parameter(description = "EST Profile UUID") @PathVariable UUID uuid)
            throws NotFoundException;

    @Operation(operationId = "bulkDisableEstProfiles", summary = "Disable multiple EST Profiles")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "EST Profiles disabled")})
    @PatchMapping(path = "/disable", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void bulkDisableEstProfiles(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "EST Profile UUIDs",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UUID.class)), examples = {
                    @ExampleObject(
                            value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<UUID> uuids);

    @Operation(operationId = "updateEstProfileRaProfile", summary = "Set the RA Profile of an EST Profile",
            description = "The RA Profile serves requests sent to the EST Profile's own label")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "RA Profile set")})
    @PatchMapping(path = "/{uuid}/raProfiles/{raProfileUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateEstProfileRaProfile(@Parameter(description = "EST Profile UUID") @PathVariable UUID uuid,
            @Parameter(description = "RA Profile UUID") @PathVariable UUID raProfileUuid) throws NotFoundException;
}
