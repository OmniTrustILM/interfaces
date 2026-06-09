package com.otilm.api.model.common.events.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ScheduledJobFinishedEventData implements EventData {
    @Schema(description = "Name of the scheduled job", requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobName;

    @Schema(description = "Type of scheduled job (job processor name)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobType;

    @Schema(description = "Execution status of last job triggered task", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(description = "UUID of user that  the scheduled job", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID userUuid;
}
