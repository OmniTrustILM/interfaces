package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.notification.NotificationRequestDto;
import com.otilm.api.model.client.notification.NotificationResponseDto;
import com.otilm.api.model.common.ErrorMessageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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

@RequestMapping("/v1/notifications")
@Tag(name = "Internal Notification",
        description = "Internal Notifications API that manages notifications for logged user in the platform. Note " +
                "that this API does not manage nor trigger external notifications. For external notifications, please " +
                "refer to the External Notification Management API."
)
public interface NotificationController extends AuthProtectedController {
    @Operation(summary = "List notifications for logged user")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of notifications")})
    @GetMapping(produces = {"application/json"})
    NotificationResponseDto listNotifications(@Parameter(in = ParameterIn.QUERY, description = "Show only unread notifications") NotificationRequestDto request);

    @Operation(summary = "Delete a notification for logged user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notification deleted"),
            @ApiResponse(responseCode = "404", description = "Notification not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @DeleteMapping(path = "/{uuid}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteNotification(@Parameter(description = "Notification UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Mark notification as read for logged user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notification marked as read"),
            @ApiResponse(responseCode = "404", description = "Notification not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PatchMapping(path = "/{uuid}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void markNotificationAsRead(@Parameter(description = "Notification UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Delete a list of notifications for logged user")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Notifications deleted")})
    @DeleteMapping(produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void bulkDeleteNotification(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Notifications UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples={@ExampleObject(value="[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<String> uuids);

    @Operation(summary = "Mark a list of notifications as read for logged user")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Notifications marked as read")})
    @PatchMapping(produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void bulkMarkNotificationAsRead(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Notifications UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples={@ExampleObject(value="[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<String> uuids);
}
