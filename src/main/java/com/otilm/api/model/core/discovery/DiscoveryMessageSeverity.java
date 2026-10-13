package com.otilm.api.model.core.discovery;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

/**
 * How much a {@link DiscoveryMessageDto} matters.
 *
 * <p>
 * <b>Deliberately scoped to Discovery.</b> Severity ladders in this platform have never been shared — compliance, audit
 * and connector logging each keep their own — so this vocabulary is shaped by what a Discovery run needs to say and
 * should not quietly become the platform's.
 *
 * <p>
 * <b>Assigned by the platform, never by a Discovery Provider.</b> Every message and its code are the platform's own:
 * what an event means for a run is the platform's judgement, so severity never crosses the connector contract.
 */
@Schema(enumAsRef = true)
public enum DiscoveryMessageSeverity implements IPlatformEnum {

    INFO("info", "Info", "Worth recording, but nothing went wrong."),
    WARNING("warning", "Warning",
            "Something was skipped or degraded and the run carried on. The run can still complete."),
    ERROR("error", "Error", "Something failed outright. Present on runs that ended badly, and on runs that "
            + "recovered but lost work along the way.");

    private static final DiscoveryMessageSeverity[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Discovery run message severity", examples = {"warning"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;

    DiscoveryMessageSeverity(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @Override
    @JsonValue
    public String getCode() {
        return this.code;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @JsonCreator
    public static DiscoveryMessageSeverity findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(severity -> severity.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown Discovery message severity {}", code)));
    }
}
