package com.otilm.api.model.core.discovery;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum DiscoveryStatus implements IPlatformEnum {

    IN_PROGRESS("inProgress", "In Progress"),
    PROCESSING("processing", "Processing"),
    FAILED("failed", "Failed"),
    COMPLETED("completed", "Completed"),
    WARNING("warning", "Warning")
    ;

    private static final DiscoveryStatus[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Discovery Status",
            examples = {"completed"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;

    DiscoveryStatus(String code, String label) {
        this(code, label,null);
    }

    DiscoveryStatus(String code, String label, String description) {
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
    public static DiscoveryStatus findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown Discovery status {}", code)));
    }
}
