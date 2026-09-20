package com.otilm.api.model.common.signature.parameters.pades;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

@Schema(name = "PadesTextVerticalAlignment", enumAsRef = true,
        description = "Vertical alignment of the caption within its area")
public enum TextVerticalAlignment implements IPlatformEnum {

    TOP(Codes.TOP, "Top", "The caption sits at the top of the area"),
    MIDDLE(Codes.MIDDLE, "Middle", "The caption is centred vertically in the area"),
    BOTTOM(Codes.BOTTOM, "Bottom", "The caption sits at the bottom of the area");

    public static class Codes {

        private Codes() {
        }

        public static final String TOP = "top";
        public static final String MIDDLE = "middle";
        public static final String BOTTOM = "bottom";
    }

    private static final TextVerticalAlignment[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    TextVerticalAlignment(String code, String label, String description) {
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
    public static TextVerticalAlignment findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown text vertical alignment {}", code)));
    }
}
