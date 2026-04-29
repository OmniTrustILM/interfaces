package com.czertainly.api.model.core.workflows;

import com.czertainly.api.model.common.PaginationResponseDto;
import com.czertainly.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class EventHistoryDto {

    @Schema(description = "Time at which the event firing started.", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime startedAt;

    @Schema(description = "Time at which the event firing finished.")
    private OffsetDateTime finishedAt;

    @Schema(description = "Status of the event firing.", requiredMode = Schema.RequiredMode.REQUIRED)
    private EventStatus status;

    @Schema(description = "Resource of the objects that triggers have been evaluated on.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Resource resource;

    @Schema(description = "Number of objects evaluated.", requiredMode = Schema.RequiredMode.REQUIRED)
    private int objectsEvaluated;

    @Schema(description = "Number of objects matched at least by one trigger.", requiredMode = Schema.RequiredMode.REQUIRED)
    private int objectsMatched;

    @Schema(description = "Number of objects matched by ignore triggers.", requiredMode = Schema.RequiredMode.REQUIRED)
    private int objectsIgnored;

    @Schema(description = "Paginated list of trigger evaluations on individual objects.", requiredMode = Schema.RequiredMode.REQUIRED)
    private PaginationResponseDto<TriggerHistoryObjectSummaryDto> objectHistories;
}
