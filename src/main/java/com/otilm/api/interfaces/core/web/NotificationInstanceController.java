package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.AttributeException;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.common.UuidDto;
    import com.otilm.api.model.common.attribute.common.DataAttribute;
import com.otilm.api.model.core.notification.NotificationInstanceDto;
import com.otilm.api.model.core.notification.NotificationInstanceRequestDto;
import com.otilm.api.model.core.notification.NotificationInstanceUpdateRequestDto;
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

@RequestMapping("/v1/notificationInstances")
@Tag(name = "External Notification Management",
        description = "External Notification Management API for managing notification instances that can be used " +
                "to notify recipient(s) about events that were triggered in the platform. Note that this API does " +
                "not handle internal notifications. For internal notifications, please refer to the Internal " +
                "Notification API"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "502", description = "Connector Error", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
        @ApiResponse(responseCode = "503", description = "Connector Communication Error", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
})
public interface NotificationInstanceController extends AuthProtectedController {

    @Operation(summary = "List of available Notification instances")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of Notification instances")})
    @GetMapping(produces = {"application/json"})
    List<NotificationInstanceDto> listNotificationInstances();

    @Operation(summary = "Details of an Notification instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification instance details retrieved"),
            @ApiResponse(responseCode = "404", description = "Notification instance not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping(path = "/{uuid}", produces = {"application/json"})
    NotificationInstanceDto getNotificationInstance(
            @Parameter(description = "Notification instance UUID") @PathVariable String uuid) throws ConnectorException, NotFoundException;

    @Operation(summary = "Add Notification instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New Notification instance added", content = @Content(schema = @Schema(implementation = UuidDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),
            @ApiResponse(responseCode = "404", description = "Connector not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<?> createNotificationInstance(
            @RequestBody NotificationInstanceRequestDto request) throws AlreadyExistException, ConnectorException, AttributeException, NotFoundException;

    @Operation(summary = "Edit Notification instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification instance details updated"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),
            @ApiResponse(responseCode = "404", description = "Notification instance or connector not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PutMapping(path = "/{uuid}", consumes = {"application/json"}, produces = {"application/json"})
    NotificationInstanceDto editNotificationInstance(
            @Parameter(description = "Notification instance UUID") @PathVariable String uuid,
            @RequestBody NotificationInstanceUpdateRequestDto request) throws ConnectorException, AttributeException, NotFoundException;

    @Operation(summary = "Delete Notification instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notification instance deleted"),
            @ApiResponse(responseCode = "404", description = "Notification instance not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{uuid}", produces = {"application/json"})
    void deleteNotificationInstance(
            @Parameter(description = "Notification instance UUID") @PathVariable String uuid) throws ConnectorException, NotFoundException;

    @Operation(summary = "List of mapping attributes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of mapping attributes"),
            @ApiResponse(responseCode = "404", description = "Connector not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping(path = "/attributes/mapping/{connectorUuid}/{kind}", produces = {"application/json"})
    List<DataAttribute> listMappingAttributes(
            @Parameter(description = "Connector UUID") @PathVariable String connectorUuid,
            @Parameter(description = "Kind") @PathVariable String kind) throws ConnectorException, NotFoundException;
}
