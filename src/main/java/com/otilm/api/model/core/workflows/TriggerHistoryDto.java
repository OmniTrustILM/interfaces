package com.otilm.api.model.core.workflows;

import com.otilm.api.model.common.UuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TriggerHistoryDto extends UuidDto {
    @Schema(description = "UUID of the trigger.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String triggerUuid;

    @Schema(description = "UUID of the object that the trigger has been evaluated on.")
    private String objectUuid;

    @Schema(description = "Reference UUID of the object that the trigger has been evaluated on.")
    private String referenceObjectUuid;

    @Schema(description = "All conditions in the trigger have been matched.", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean conditionsMatched;

    @Schema(description = "All actions in the trigger have been performed.", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean actionsPerformed;

    @Schema(description = "Time at which has the trigger been triggered", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime triggeredAt;

    @Schema(description = "Additional message. ")
    private String message;

    @Schema(description = "List of records for each action that has not been performed and each condition that has not been evaluated.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<TriggerHistoryRecordDto> records = new ArrayList<>();
 }
