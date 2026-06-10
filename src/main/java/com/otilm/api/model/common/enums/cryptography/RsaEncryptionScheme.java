package com.otilm.api.model.common.enums.cryptography;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum RsaEncryptionScheme implements IPlatformEnum {
    PKCS1_v1_5("PKCS1-v1_5", "PKCS#1 v1.5", "Deterministic RSA encryption scheme"),
    OAEP("OAEP", "OAEP", "Optimal Asymmetric Encryption Padding");

    @Schema(description = "Type of the RSA encryption scheme",
            examples = {"OAEP"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;

    RsaEncryptionScheme(String code, String label, String description) {
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
    public static RsaEncryptionScheme findByCode(String code) {
        return Arrays.stream(RsaEncryptionScheme.values())
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown RSA encryption scheme code {}", code)));
    }
}
