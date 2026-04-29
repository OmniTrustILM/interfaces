package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.notification.NotificationProfileDetailDto;
import com.otilm.api.model.client.notification.NotificationProfileRequestDto;
import com.otilm.api.model.client.notification.NotificationProfileResponseDto;
import com.otilm.api.model.client.notification.NotificationProfileUpdateRequestDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.core.scheduler.PaginationRequestDto;
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
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1/notificationProfiles")
@Tag(name = "Notification profile inventory", description = "Notification profile inventory API")
public interface NotificationProfileController extends AuthProtectedController {

    @Operation(summary = "List Notification profiles")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of all the notification profiles")})
    @GetMapping(produces = {"application/json"})
    NotificationProfileResponseDto listNotificationProfiles(final PaginationRequestDto paginationRequestDto);

    @Operation(summary = "Get Notification profile details")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Notification profile retrieved"), @ApiResponse(responseCode = "404", description = "Notification profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/{uuid}", produces = {"application/json"})
    NotificationProfileDetailDto getNotificationProfile(@Parameter(description = "Notification profile UUID") @PathVariable String uuid, @Parameter(in = ParameterIn.QUERY, description = "Select specific version of the notification profile") Integer version) throws NotFoundException;

    @Operation(summary = "Delete notification profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Notification profile deleted"), @ApiResponse(responseCode = "404", description = "Notification profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))), @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @DeleteMapping(path = "/{uuid}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteNotificationProfile(@Parameter(description = "Notification profile UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Create Notification profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "New Notification profile created", content = @Content(schema = @Schema(implementation = NotificationProfileDetailDto.class))), @ApiResponse(responseCode = "404", description = "Notification profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))), @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<?> createNotificationProfile(@Valid @RequestBody NotificationProfileRequestDto notificationProfileRequestDto) throws NotFoundException, AlreadyExistException;

    @Operation(summary = "Edit Notification profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Notification profile updated", content = @Content(schema = @Schema(implementation = NotificationProfileDetailDto.class))), @ApiResponse(responseCode = "404", description = "Notification profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))), @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PutMapping(path = "/{uuid}", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<?> editNotificationProfile(@Parameter(description = "Notification profile UUID") @PathVariable String uuid, @Valid @RequestBody NotificationProfileUpdateRequestDto notificationProfileUpdateRequestDto) throws NotFoundException;

}
