package com.czertainly.api.model.core.workflows;

import com.czertainly.api.model.core.scheduler.PaginationRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EventHistoryRequestDto {

    @Schema(description = "Pagination parameters for the list of event history entries.")
    private PaginationRequestDto pagination = new PaginationRequestDto();

    @Schema(description = "Pagination parameters for the list of individual object histories within each event history entry.")
    private PaginationRequestDto objectsPagination = new PaginationRequestDto();
}
