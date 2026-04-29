package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.other.ResourceEvent;
import com.otilm.api.model.core.workflows.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/v1/workflows")
@Tag(name = "Workflow Triggers Management", description = "Workflow Triggers Management API")
public interface TriggerController extends AuthProtectedController {
    @Operation(summary = "List Triggers")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of triggers")})
    @GetMapping(path = "/triggers", produces = {"application/json"})
    List<TriggerDto> listTriggers(@RequestParam(required = false) Resource resource);

    @Operation(summary = "Create Trigger")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trigger created"),
            @ApiResponse(responseCode = "404", description = "Rule or Action not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PostMapping(path = "/triggers", consumes = {"application/json"}, produces = {"application/json"})
    TriggerDetailDto createTrigger(@RequestBody TriggerRequestDto request) throws NotFoundException, AlreadyExistException;

    @Operation(summary = "Get Trigger details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trigger details retrieved"),
            @ApiResponse(responseCode = "404", description = "Trigger not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping(path = "/triggers/{triggerUuid}", produces = {"application/json"})
    TriggerDetailDto getTrigger(@Parameter(description = "Trigger UUID") @PathVariable String triggerUuid) throws NotFoundException;

    @Operation(summary = "Update Trigger")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trigger updated"),
            @ApiResponse(responseCode = "404", description = "Trigger, Rule or Action not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PutMapping(path = "/triggers/{triggerUuid}", consumes = {"application/json"}, produces = {"application/json"})
    TriggerDetailDto updateTrigger(@Parameter(description = "Trigger UUID") @PathVariable String triggerUuid, @RequestBody UpdateTriggerRequestDto request) throws NotFoundException;

    @Operation(summary = "Delete Trigger")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Trigger deleted"),
            @ApiResponse(responseCode = "404", description = "Trigger not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @DeleteMapping(path = "/triggers/{triggerUuid}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteTrigger(@Parameter(description = "Trigger UUID") @PathVariable String triggerUuid) throws NotFoundException;

    @Operation(summary = "Get Trigger History")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Trigger History retrieved")})
    @GetMapping(path = "/triggers/{triggerUuid}/history/{associationObjectUuid}", produces = {"application/json"})
    List<TriggerHistoryDto> getTriggerHistory(@Parameter(description = "Trigger UUID") @PathVariable String triggerUuid, @Parameter(description = "Trigger Association Object UUID") @PathVariable String associationObjectUuid);

    @Operation(summary = "Get Trigger History Summary")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trigger History Summary retrieved"),
            @ApiResponse(responseCode = "404", description = "Trigger not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping(path = "/triggers/history/{associationObjectUuid}", produces = {"application/json"})
    TriggerHistorySummaryDto getTriggerHistorySummary(@Parameter(description = "Trigger Association Object UUID") @PathVariable String associationObjectUuid) throws NotFoundException;

    @Operation(summary = "Associate event with triggers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trigger associations created"),
            @ApiResponse(responseCode = "404", description = "Rule or Action not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PostMapping(path = "/events", consumes = {"application/json"}, produces = {"application/json"})
    void associateEventTriggers(@RequestBody TriggerEventAssociationRequestDto request) throws NotFoundException;

    @Operation(summary = "Get event triggers associations for resource object")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trigger associations retrieved"),
    })
    @GetMapping(path = "/events/{resource}/{associationObjectUuid}", produces = {"application/json"})
    Map<ResourceEvent, List<UUID>> getEventTriggersAssociations(@Parameter(description = "Resource", required = true) @PathVariable Resource resource, @Parameter(description = "Association object UUID", required = true) @PathVariable UUID associationObjectUuid);


}
