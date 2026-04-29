package com.otilm.api.model.core.cryptography.key;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum KeyState implements IPlatformEnum {
    PRE_ACTIVE("pre-active", "Pre-active"),
    ACTIVE("active", "Active"),
    DEACTIVATED("deactivated", "Deactivated"),
    COMPROMISED("compromised", "Compromised"),
    DESTROYED("destroyed", "Destroyed"),
    DESTROYED_COMPROMISED("destroyedCompromised", "Destroyed Compromised");

    private static final KeyState[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "State of the key",
            examples = {"active"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;

    KeyState(String code, String label) {
        this(code, label,null);
    }

    KeyState(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @JsonCreator
    public static KeyState findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown key state {}", code)));
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
