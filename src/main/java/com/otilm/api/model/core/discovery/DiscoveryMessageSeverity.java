package com.otilm.api.model.core.discovery;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

/**
 * How much a {@link DiscoveryMessageDto} matters. Scoped to discovery on purpose, as the platform's other severity
 * ladders are to theirs, and assigned by the platform, never by a Discovery Provider.
 */
@Schema(enumAsRef = true)
public enum DiscoveryMessageSeverity implements IPlatformEnum {

    INFO("info", "Info", "Worth recording, but nothing went wrong."),
    WARNING("warning", "Warning",
            "Something was skipped or degraded, or the run was cancelled without a user asking for it."),
    ERROR("error", "Error", "Something failed outright, such as the run itself.");

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
