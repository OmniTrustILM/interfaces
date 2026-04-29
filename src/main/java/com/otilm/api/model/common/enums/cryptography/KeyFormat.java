package com.otilm.api.model.common.enums.cryptography;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum KeyFormat implements IPlatformEnum {
    RAW("Raw", "Raw", "Encoded key in raw format"),
    SPKI("SubjectPublicKeyInfo", "SubjectPublicKeyInfo", "DER-encoded ASN.1 SubjectPublicKeyInfo of the public key"),
    PRKI("PrivateKeyInfo", "PrivateKeyInfo", "DER-encoded ASN.1 PrivateKeyInfo of the private key"),
    EPRKI("EncryptedPrivateKeyInfo", "EncryptedPrivateKeyInfo", "DER-encoded ASN.1 EncryptedPrivateKeyInfo of the private key"),
    CUSTOM("Custom", "Custom", "Custom, external, specific data");

    private static final KeyFormat[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Key format code",
            examples = {"Raw"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;

    KeyFormat(String code, String label, String description) {
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
    public static KeyFormat findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown key format code {}", code)));
    }
}
