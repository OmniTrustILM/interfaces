package com.otilm.api.model.core.logging.enums;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum AuthMethod implements IPlatformEnum {
    NONE("none", "None"),
    CERTIFICATE("certificate", "Certificate"),
    TOKEN("token", "Token"),
    SESSION("session", "Session"),
    API_KEY("apiKey", "API Key"),
    USER_PROXY("userProxy", "User proxy");

    private static final AuthMethod[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    AuthMethod(String code, String label) {
        this(code, label,null);
    }

    AuthMethod(String code, String label, String description) {
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
    public static AuthMethod findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(a -> a.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown Auth method code {}", code)));
    }
}
