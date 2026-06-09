package com.otilm.api.model.common.enums.cryptography;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum KeyType implements IPlatformEnum {
    SECRET_KEY("Secret", "Secret key", "Symmetric secret key"),
    PUBLIC_KEY("Public", "Public key", "Asymmetric public key"),
    PRIVATE_KEY("Private", "Private key", "Asymmetric private key"),
    SPLIT_KEY("Split", "Split key", "Secret or private key split into parts");

    private static final KeyType[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Key type code",
            examples = {"Secret"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;

    KeyType(String code, String label, String description) {
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
    public static KeyType findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown key type code {}", code)));
    }
}
