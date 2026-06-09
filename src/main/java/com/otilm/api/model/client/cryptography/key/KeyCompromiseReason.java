package com.otilm.api.model.client.cryptography.key;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum KeyCompromiseReason implements IPlatformEnum {
    UNAUTHORIZED_DISCLOSURE("disclosure", "Unauthorized disclosure"),
    UNAUTHORIZED_MODIFICATION("modification", "Unauthorized modification"),

    UNAUTHORIZED_SUBSTITUTION("substitution", "Unauthorized substitution"),

    UNAUTHORIZED_USE_OF_SENSITIVE_DATA("use_of_sensitive_data", "Unauthorized use of sensitive data");

    private static final KeyCompromiseReason[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Reason for compromise",
            examples = {"Unauthorized Disclosure"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;

    KeyCompromiseReason(String code, String label) {
        this(code, label,null);
    }

    KeyCompromiseReason(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @JsonCreator
    public static KeyCompromiseReason findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown key compromise reason {}", code)));
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
}
