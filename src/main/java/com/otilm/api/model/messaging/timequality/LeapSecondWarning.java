package com.otilm.api.model.messaging.timequality;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum LeapSecondWarning implements IPlatformEnum {

    NONE(Codes.NONE, "None", "No leap second announced; also set when NTP leap indicators conflict across servers — the conflict is reflected by TimeQualityStatus.DEGRADED"),
    POSITIVE(Codes.POSITIVE, "Positive", "Positive leap second announced: one second will be inserted at end of day"),
    NEGATIVE(Codes.NEGATIVE, "Negative", "Negative leap second announced: one second will be deleted at end of day"),
    ;

    private static final LeapSecondWarning[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    LeapSecondWarning(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @JsonCreator
    public static LeapSecondWarning findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown leap second warning {}", code)));
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

    public static class Codes {
        public static final String NONE = "none";
        public static final String POSITIVE = "positive";
        public static final String NEGATIVE = "negative";

        private Codes() {
        }
    }
}
