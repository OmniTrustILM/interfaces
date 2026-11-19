package com.otilm.api.model.client.discovery;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

/**
 * How much of the target a discovery covers: all of it, or only what was created or changed within a window.
 */
@Schema(enumAsRef = true)
public enum DiscoveryWindowMode implements IPlatformEnum {
    FULL("full", "Full", "Discover everything the target holds"),
    SINCE_LAST_SUCCESSFUL_RUN("sinceLastSuccessfulRun", "Since the last successful run",
            "Discover what was created or changed since the scheduled job's last successful run; available when "
                    + "scheduling. A run that ended with warnings counts as successful, so items it missed may need a "
                    + "full run"),
    LOOKBACK("lookback", "Lookback",
            "Discover what was created or changed within the configured duration before the run starts");

    private static final DiscoveryWindowMode[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Discovery window mode", examples = {"lookback"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;

    @Schema(description = "Name", examples = {"Lookback"})
    private final String label;

    @Schema(description = "Description")
    private final String description;

    DiscoveryWindowMode(String code, String label, String description) {
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
    public static DiscoveryWindowMode findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown discovery window mode {}", code)));
    }
}
