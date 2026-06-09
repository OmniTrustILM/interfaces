package com.otilm.api.model.core.scheduler;

import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.scheduler.SchedulerJobExecutionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ScheduledJobHistoryDto {

    @Schema(
            description = "Scheduled job UUID"
    )
    private UUID jobUuid;

    @Schema(
            description = "Start time of triggered task",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Date startTime;

    @Schema(
            description = "End time of triggered task"
    )
    private Date endTime;

    @Schema(
            description = "Execution status of triggered task",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private SchedulerJobExecutionStatus status;

    @Schema(
            description = "Message explaining result status"
    )
    private String resultMessage;

    @Schema(
            description = "Result object type"
    )
    private Resource resultObjectType;

    @Schema(
            description = "Result object identification (UUID)"
    )
    private List<String> resultObjectIdentification;

}
