package com.otilm.api.model.scheduler;

import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum SchedulerJobExecutionStatus implements IPlatformEnum {

    STARTED("started", "Started"),
    SUCCESS("succeeded", "Succeeded"),
    FAILED("failed", "Failed");

    private static final SchedulerJobExecutionStatus[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Scheduled job execution status code",
            examples = {"Secret"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;

    SchedulerJobExecutionStatus(String code, String label) {
        this(code, label,null);
    }

    SchedulerJobExecutionStatus(String code, String label, String description) {
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
        return this.description;
    }

    @JsonCreator
    public static SchedulerJobExecutionStatus fromCode(String code) {
        return Arrays.stream(VALUES)
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported scheduled job execution status %s.", code)));
    }
}
