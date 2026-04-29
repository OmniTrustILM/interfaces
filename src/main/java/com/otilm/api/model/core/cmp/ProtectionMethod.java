package com.otilm.api.model.core.cmp;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/**
 * Protection methods for CMP messages
 */
@Schema(enumAsRef = true)
public enum ProtectionMethod implements IPlatformEnum {
    SHARED_SECRET("sharedSecret", "Shared Secret"),
    SIGNATURE("signature", "Signature");

    private static final ProtectionMethod[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    ProtectionMethod(String code, String label) {
        this(code, label,null);
    }

    ProtectionMethod(String code, String label, String description) {
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
        return this.description;
    }

    @JsonCreator
    public static ProtectionMethod findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown CMP Protection Method code {}", code)));
    }
}
