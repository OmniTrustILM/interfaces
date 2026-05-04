package com.czertainly.api.model.core.workflows;

import com.czertainly.api.model.common.NameAndUuidDto;
import com.czertainly.api.model.core.other.ResourceEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ObjectEventHistoryDto {

    @Schema(description = "Resource event that was triggered.", requiredMode = Schema.RequiredMode.REQUIRED)
    private ResourceEvent event;

    @Schema(description = "Trigger that evaluated the event.", requiredMode = Schema.RequiredMode.REQUIRED)
    private NameAndUuidDto trigger;

    @Schema(description = "Origin that caused the event to be triggered.")
    private TriggeredEventOriginDto triggeredEventOrigin;

    @Schema(description = "All conditions in the trigger have been matched.", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean conditionsMatched;

    @Schema(description = "All actions in the trigger have been performed.", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean actionsPerformed;

    @Schema(description = "Time at which the event was triggered.", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime triggeredAt;

    @Schema(description = "Additional message.")
    private String message;

    @Schema(description = "If trigger was sending notifications, confirmation whether notification has been sent successfully or not.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean notificationsSent;

    @Schema(description = "List of records for each condition that has not been matched and each action that has not been performed.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<TriggerHistoryRecordDto> records = new ArrayList<>();
}
