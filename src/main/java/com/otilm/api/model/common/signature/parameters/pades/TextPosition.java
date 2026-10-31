package com.otilm.api.model.common.signature.parameters.pades;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

@Schema(name = "PadesTextPosition", enumAsRef = true,
        description = "Side of the stamp image the caption sits on, when a visible signature draws both")
public enum TextPosition implements IPlatformEnum {

    TOP(Codes.TOP, "Top", "The caption is drawn above the image"),
    BOTTOM(Codes.BOTTOM, "Bottom", "The caption is drawn below the image"),
    LEFT(Codes.LEFT, "Left", "The caption is drawn to the left of the image"),
    RIGHT(Codes.RIGHT, "Right", "The caption is drawn to the right of the image");

    public static class Codes {

        private Codes() {
        }

        public static final String TOP = "top";
        public static final String BOTTOM = "bottom";
        public static final String LEFT = "left";
        public static final String RIGHT = "right";
    }

    private static final TextPosition[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    TextPosition(String code, String label, String description) {
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
    public static TextPosition findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(ValidationError.create("Unknown text position {}", code)));
    }
}
