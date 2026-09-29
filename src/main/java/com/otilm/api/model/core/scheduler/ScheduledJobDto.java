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

    @Schema(description = "State of the job's trigger as the scheduler service reported it for this response. "
            + "'scheduled': the scheduler holds a live trigger. 'paused': the trigger is paused, which is what "
            + "disabling the job does. 'blocked': the trigger waits for a run still in progress. 'error': the "
            + "scheduler could not fire the trigger and stopped trying. 'complete': the trigger has no fire time "
            + "left. 'notScheduled': the scheduler holds no trigger for this job -- the normal end of a one-time "
            + "job once its run has succeeded, since the platform then removes it from the scheduler; any other "
            + "job will not fire until it is registered again. 'unknown': the scheduler could not be read for "
            + "this response, or it reported no state for the trigger (a scheduler that predates this field); "
            + "nextFireTime and previousFireTime are then absent.", requiredMode = Schema.RequiredMode.REQUIRED)
    private ScheduledJobScheduleState scheduleState;

    @Schema(description = "When the scheduler will next fire the job: the fire time the scheduler holds for the "
            + "job's trigger, computed by the scheduler from the trigger's own schedule, not projected from the "
            + "stored CRON expression. Absent while the schedule state is 'paused', 'notScheduled' or 'unknown', "
            + "and when the trigger has no further fire time. While the state is 'scheduled', either of two signs "
            + "means the scheduler is not firing: this value is in the past, or previousFireTime is older than one "
            + "period of the schedule. Check both: a scheduler that is alive but stuck keeps moving this value to "
            + "about now, so it may be only seconds in the past.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Instant nextFireTime;

    @Schema(description = "When the scheduler last fired the job, as the scheduler service reported it. Absent "
            + "when the schedule state is 'unknown' or 'notScheduled' and until the trigger has fired once. A "
            + "firing newer than both lastExecutionStartTime and lastSkippedAt, which the platform stamps after the "
            + "firing it acts on, has not reached the platform.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Instant previousFireTime;

    @Schema(description = "When the last recorded run of the job started, by the platform's own clock as it began "
            + "the run, after the scheduler fired it; absent while the job has no run in its history.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Instant lastExecutionStartTime;

    @Schema(description = "When the job last ran and declined the run -- nothing to do, or a condition it could not "
            + "proceed under -- by the platform's own clock as it recorded the skip, after the scheduler fired the "
            + "run. A declined run leaves no history row; this is what shows the job is alive. Absent until the job "
            + "has declined a run.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Instant lastSkippedAt;

    @Schema(description = "Why the run at lastSkippedAt was declined, in the task's own words; absent with it.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String lastSkipReason;

}
