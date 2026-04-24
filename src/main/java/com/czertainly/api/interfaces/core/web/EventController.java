package com.czertainly.api.interfaces.core.web;

import com.czertainly.api.interfaces.AuthProtectedController;
import com.czertainly.api.model.common.PaginationResponseDto;
import com.czertainly.api.model.core.auth.Resource;
import com.czertainly.api.model.core.scheduler.PaginationRequestDto;
import com.czertainly.api.model.core.workflows.ObjectEventHistoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("/v1/workflows")
@Tag(name = "Event", description = "Event API")
public interface EventController extends AuthProtectedController {

    @Operation(summary = "Get event history for a resource object")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Event history retrieved")})
    @GetMapping(path = "/events/history/{resource}/{uuid}", produces = {"application/json"})
    PaginationResponseDto<ObjectEventHistoryDto> getEventHistory(
            @Parameter(description = "Resource", required = true) @PathVariable Resource resource,
            @Parameter(description = "Object UUID", required = true) @PathVariable UUID uuid,
            PaginationRequestDto pagination
    );
}
