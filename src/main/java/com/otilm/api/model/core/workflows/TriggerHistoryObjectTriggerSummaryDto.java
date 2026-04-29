package com.otilm.api.model.core.workflows;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class TriggerHistoryObjectTriggerSummaryDto {
    @Schema(description = "UUID of the object that the trigger has been evaluated on.", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID triggerUuid;

    @Schema(description = "Reference UUID of the object that the trigger has been evaluated on.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String triggerName;

    @Schema(description = "Time at which has the trigger been triggered", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime triggeredAt;

    @Schema(description = "Additional message. ")
    private String message;

    @Schema(description = "List of records for each action that has not been performed and each condition that has not been evaluated.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<TriggerHistoryRecordDto> records = new ArrayList<>();
}
