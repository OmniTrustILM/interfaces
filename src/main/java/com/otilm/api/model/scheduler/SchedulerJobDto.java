package com.otilm.api.model.scheduler;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SchedulerJobDto {

    private UUID uuidJob;

    private String jobName;

    private String cronExpression;

    private String classNameToBeExecuted;

    /** When the trigger next fires, as Quartz holds it; absent without a trigger or when no fire time is left. */
    private Instant nextFireTime;

    /** When the trigger last fired; absent until it has fired once. */
    private Instant previousFireTime;

    /**
     * Quartz's own state of the job's trigger; {@code NONE} when the scheduler holds no trigger for the job. Absent in
     * the answer of a scheduler that predates the field.
     */
    private SchedulerTriggerState triggerState;

    public SchedulerJobDto(String jobName, String cronExpression, String classNameToBeExecuted) {
        this(null, jobName, cronExpression, classNameToBeExecuted);
    }

    /** The signature callers used before the trigger facts were added; the facts stay unreported. */
    public SchedulerJobDto(UUID uuidJob, String jobName, String cronExpression, String classNameToBeExecuted) {
        this(uuidJob, jobName, cronExpression, classNameToBeExecuted, null, null, null);
    }

}
