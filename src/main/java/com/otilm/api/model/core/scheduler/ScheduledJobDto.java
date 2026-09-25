package com.otilm.api.model.core.scheduler;

import com.otilm.api.model.scheduler.SchedulerJobExecutionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduledJobDto {

    @Schema(description = "UUID of the scheduled job", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID uuid;

    @Schema(description = "Name of the scheduled job", requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobName;

    @Schema(description = "Type of scheduled job (job processor name)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobType;

    @Schema(description = "CRON expression representing configuration of pattern how to run scheduled job",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String cronExpression;

    @Schema(description = "Status of the scheduled job. True = Enabled, False = Disabled",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean enabled;

    @Schema(description = "Is scheduled job triggered only once", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean oneTime;

    @Schema(description = "Is system scheduled job", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean system;

    @Schema(description = "Execution status of last job triggered task", requiredMode = Schema.RequiredMode.REQUIRED)
    private SchedulerJobExecutionStatus lastExecutionStatus;

    @Schema(description = "Time at which the job is next due to run, projected from its stored CRON expression -- "
            + "not an observed value read back from the scheduler, so it does not confirm the scheduler still "
            + "holds a live trigger for this job. Absent when the job is disabled, when it is a one-time job "
            + "whose run has succeeded, or when its CRON expression yields no further run. Evaluated in core's "
            + "JVM default timezone; assumes this matches the timezone the scheduler service had when the job's "
            + "trigger was created -- if the two differ, this value can be off by the difference between them.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Instant nextFireTime;

}
