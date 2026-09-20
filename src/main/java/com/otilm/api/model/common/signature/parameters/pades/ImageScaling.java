package com.otilm.api.model.common.signature.parameters.pades;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

@Schema(name = "PadesImageScaling", enumAsRef = true,
        description = "How the stamp image is fitted into the signature box")
public enum ImageScaling implements IPlatformEnum {

    STRETCH(Codes.STRETCH, "Stretch", "The image is stretched to fill the box, whatever its aspect ratio"),
    ZOOM_AND_CENTER(Codes.ZOOM_AND_CENTER, "Zoom and center",
            "The image is scaled to fit the box, keeping its aspect ratio, and centred in it"),
    CENTER(Codes.CENTER, "Center", "The image keeps its natural size and is centred in the box");

    public static class Codes {

        private Codes() {
        }

        public static final String STRETCH = "stretch";
        public static final String ZOOM_AND_CENTER = "zoom_and_center";
        public static final String CENTER = "center";
    }

    private static final ImageScaling[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    ImageScaling(String code, String label, String description) {
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
    public static ImageScaling findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(ValidationError.create("Unknown image scaling {}", code)));
    }
}
