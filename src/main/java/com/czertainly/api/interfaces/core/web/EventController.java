package com.czertainly.api.interfaces.core.web;

import com.czertainly.api.exception.NotFoundException;
import com.czertainly.api.interfaces.AuthProtectedController;
import com.czertainly.api.model.common.ErrorMessageDto;
import com.czertainly.api.model.common.PaginationResponseDto;
import com.czertainly.api.model.core.auth.Resource;
import com.czertainly.api.model.core.other.ResourceEvent;
import com.czertainly.api.model.core.scheduler.PaginationRequestDto;
import com.czertainly.api.model.core.workflows.EventHistoryDto;
import com.czertainly.api.model.core.workflows.EventHistoryRequestDto;
import com.czertainly.api.model.core.workflows.ObjectEventHistoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/v1/workflows/events")
@Tag(name = "Workflow Event Management", description = "Workflow Event Management API")
@ApiResponses(
        value = {
                @ApiResponse(
                        responseCode = "404",
                        description = "Not Found",
                        content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
                )
        })
public interface EventController extends AuthProtectedController {

    @Operation(summary = "Get event history for a resource object")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Event history retrieved")})
    @GetMapping(path = "/history/{resource}/{uuid}", produces = {"application/json"})
    PaginationResponseDto<ObjectEventHistoryDto> getObjectEventHistory(
            @Parameter(description = "Resource", required = true) @PathVariable Resource resource,
            @Parameter(description = "Object UUID", required = true) @PathVariable UUID uuid,
            PaginationRequestDto pagination
    ) throws NotFoundException;

    @Operation(summary = "Get history of event defined in platform settings")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Event history retrieved")})
    @PostMapping(path = "/history/{event}", produces = {"application/json"})
    PaginationResponseDto<EventHistoryDto> getPlatformSettingsEventHistory(
            @Parameter(description = "Event name", required = true) @PathVariable ResourceEvent event,
            @RequestBody EventHistoryRequestDto request
    ) throws NotFoundException;

    @Operation(summary = "Get history of event defined by an object")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Event history retrieved")})
    @PostMapping(path = "/history/{event}/{resource}/{uuid}", produces = {"application/json"})
    PaginationResponseDto<EventHistoryDto> getObjectDefinedEventHistory(
            @Parameter(description = "Event name", required = true) @PathVariable ResourceEvent event,
            @Parameter(description = "Resource", required = true) @PathVariable Resource resource,
            @Parameter(description = "Object UUID", required = true) @PathVariable UUID uuid,
            @RequestBody EventHistoryRequestDto request
    ) throws NotFoundException;

}
