package com.otilm.api.model.core.scheduler;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

/**
 * The state of a scheduled job's trigger as the scheduler service last reported it, plus the two states only the
 * platform can know: that the scheduler holds no trigger for the job, and that the scheduler could not be read at all.
 */
@Schema(enumAsRef = true)
public enum ScheduledJobScheduleState implements IPlatformEnum {

    SCHEDULED("scheduled", "Scheduled", "The scheduler holds a live trigger for the job"),
    PAUSED("paused", "Paused", "The trigger is paused, which is what disabling the job does"),
    BLOCKED("blocked", "Blocked", "The trigger waits for a run of the job that is still in progress"),
    ERROR("error", "Error", "The scheduler could not fire the trigger and has stopped trying"),
    COMPLETE("complete", "Complete", "The trigger has no fire time left"),
    NOT_SCHEDULED("notScheduled", "Not scheduled", "The scheduler holds no trigger for the job"),
    UNKNOWN("unknown", "Unknown", "The scheduler could not be read");

    private static final ScheduledJobScheduleState[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Scheduled job schedule state code", examples = {"scheduled"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;

    ScheduledJobScheduleState(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @Override
    @JsonValue
    public String getCode() {
        return code;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static ScheduledJobScheduleState fromCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Unsupported scheduled job schedule state %s.", code)));
    }
}
