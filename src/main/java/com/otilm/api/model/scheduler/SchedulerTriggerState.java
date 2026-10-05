package com.otilm.api.model.scheduler;

/**
 * The state of a job's trigger as the scheduler service reads it from Quartz ({@code org.quartz.Trigger.TriggerState},
 * verbatim). {@code NONE} is what Quartz answers for a trigger it does not hold.
 */
public enum SchedulerTriggerState {

    NONE,
    NORMAL,
    PAUSED,
    COMPLETE,
    ERROR,
    BLOCKED;

}
