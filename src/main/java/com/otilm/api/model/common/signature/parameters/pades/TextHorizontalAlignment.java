package com.otilm.api.model.common.signature.parameters.pades;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

@Schema(name = "PadesTextHorizontalAlignment", enumAsRef = true,
        description = "Horizontal alignment of the caption within its area")
public enum TextHorizontalAlignment implements IPlatformEnum {

    LEFT(Codes.LEFT, "Left", "The caption lines start at the left edge of the area"),
    CENTER(Codes.CENTER, "Center", "The caption lines are centred in the area"),
    RIGHT(Codes.RIGHT, "Right", "The caption lines end at the right edge of the area");

    public static class Codes {

        private Codes() {
        }

        public static final String LEFT = "left";
        public static final String CENTER = "center";
        public static final String RIGHT = "right";
    }

    private static final TextHorizontalAlignment[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    TextHorizontalAlignment(String code, String label, String description) {
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
    public static TextHorizontalAlignment findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown text horizontal alignment {}", code)));
    }
}
