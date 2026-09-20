package com.otilm.api.model.core.settings.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

@Schema(description = "Reason the configured JWK Set could not be loaded for display", enumAsRef = true)
public enum JwkSetLoadFailure implements IPlatformEnum {
    UNAVAILABLE(Codes.UNAVAILABLE, "Unavailable",
            "The configured JWK Set endpoint could not be reached or returned an unsuccessful response"),
    INVALID(Codes.INVALID, "Invalid", "The configured JWK Set or one of its keys is invalid"),
    TOO_LARGE(Codes.TOO_LARGE, "Too large", "The configured JWK Set response exceeded the supported size limit");

    public static class Codes {
        public static final String UNAVAILABLE = "unavailable";
        public static final String INVALID = "invalid";
        public static final String TOO_LARGE = "tooLarge";

        private Codes() {
        }
    }

    private static final JwkSetLoadFailure[] VALUES = values();

    private final String code;
    private final String label;
    private final String description;

    JwkSetLoadFailure(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @Override
    @JsonValue
    public String getCode() {
        return code;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static JwkSetLoadFailure findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(value -> value.code.equals(code))
                .findFirst()
                .orElseThrow(
                        () -> new ValidationException(ValidationError.create("Unknown JWK Set load failure {}", code)));
    }
}
