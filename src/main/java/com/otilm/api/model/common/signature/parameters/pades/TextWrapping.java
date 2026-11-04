package com.otilm.api.model.common.signature.parameters.pades;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

@Schema(name = "PadesTextWrapping", enumAsRef = true,
        description = "How the caption of a visible signature is fitted into its area")
public enum TextWrapping implements IPlatformEnum {

    FONT_BASED(Codes.FONT_BASED, "Font based", "The caption keeps the font's own size and the line breaks it carries"),
    FILL_BOX(Codes.FILL_BOX, "Fill box", "The caption is scaled to fill its area, keeping the line breaks it carries"),
    FILL_BOX_AND_LINEBREAK(Codes.FILL_BOX_AND_LINEBREAK, "Fill box and line break",
            "The caption is scaled and broken into lines so that it fills its area");

    public static class Codes {

        private Codes() {
        }

        public static final String FONT_BASED = "font_based";
        public static final String FILL_BOX = "fill_box";
        public static final String FILL_BOX_AND_LINEBREAK = "fill_box_and_linebreak";
    }

    private static final TextWrapping[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    TextWrapping(String code, String label, String description) {
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
    public static TextWrapping findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(ValidationError.create("Unknown text wrapping {}", code)));
    }
}
