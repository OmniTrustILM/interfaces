package com.otilm.api.model.client.connector.v2;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum HealthStatus implements IPlatformEnum {
    UP("UP", "Up", "The service (or component) is fully operational and healthy."),
    DEGRADED("DEGRADED", "Degraded", "The service is working but with reduced functionality or performance."),
    DOWN("DOWN", "Down", "The service (or component) is not functioning correctly and is unavailable."),
    OUT_OF_SERVICE("OUT_OF_SERVICE", "Out of Service", "The service (or component) is intentionally out of service (e.g., disabled, under maintenance)."),
    UNKNOWN("UNKNOWN", "Unknown", "The health state cannot be determined.");

    private static final HealthStatus[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Health Status",
            examples = {"ok"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;

    HealthStatus(String code, String label, String description) {
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
    public static HealthStatus findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown Health status {}", code)));
    }
}
