package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.NotFoundException;
import com.otilm.api.exception.SchedulerException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.core.scheduler.PaginationRequestDto;
import com.otilm.api.model.core.scheduler.ScheduledJobDetailDto;
import com.otilm.api.model.core.scheduler.ScheduledJobHistoryResponseDto;
import com.otilm.api.model.core.scheduler.ScheduledJobsResponseDto;
import com.otilm.api.model.scheduler.UpdateScheduledJob;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1/scheduler/jobs")
@Tag(name = "Scheduled Jobs Management", description = "Scheduled Jobs Management API")
public interface SchedulerController extends AuthProtectedController {

    @Operation(summary = "List of scheduled jobs")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of scheduled jobs fetched")})
    @GetMapping(produces = {"application/json"})
    ScheduledJobsResponseDto listScheduledJobs(@Parameter(in = ParameterIn.QUERY) PaginationRequestDto pagination);

    @Operation(summary = "Scheduled job detail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Scheduled job detail retrieved"),
            @ApiResponse(responseCode = "404", description = "Scheduled job not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping(path = "/{uuid}", produces = {"application/json"})
    ScheduledJobDetailDto getScheduledJobDetail(@Parameter(description = "Scheduled job UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Edit Scheduled job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Scheduled job updated"),
            @ApiResponse(responseCode = "404", description = "Scheduled job not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PutMapping(path = "/{uuid}", consumes = {"application/json"},produces = {"application/json"})
    ScheduledJobDetailDto updateScheduledJob(@Parameter(description = "Scheduled job UUID") @PathVariable String uuid, @RequestBody UpdateScheduledJob request) throws NotFoundException, SchedulerException;

    @Operation(summary = "Delete Scheduled job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Scheduled job deleted"),
            @ApiResponse(responseCode = "404", description = "Scheduled job not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @DeleteMapping(path = "/{uuid}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteScheduledJob(@Parameter(description = "Scheduled job UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Scheduled job history")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Scheduled job history retrieved"),
            @ApiResponse(responseCode = "404", description = "Scheduled job not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping(path = "/{uuid}/history", produces = {"application/json"})
    ScheduledJobHistoryResponseDto getScheduledJobHistory(@Parameter(in = ParameterIn.QUERY) PaginationRequestDto pagination, @Parameter(description = "Scheduled job UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Enabling of Scheduled job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Scheduled job enabled"),
            @ApiResponse(responseCode = "404", description = "Scheduled job not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PatchMapping(path = "/{uuid}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void enableScheduledJob(@Parameter(description = "Scheduled job UUID") @PathVariable String uuid) throws NotFoundException, SchedulerException;

    @Operation(summary = "Disabling of Scheduled job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Scheduled job disabled"),
            @ApiResponse(responseCode = "404", description = "Scheduled job not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PatchMapping(path = "/{uuid}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void disableScheduledJob(@Parameter(description = "Scheduled job UUID") @PathVariable String uuid) throws NotFoundException, SchedulerException;

}
