package com.czertainly.api.model.common.enums.cryptography;

import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum SignatureAlgorithm implements IPlatformEnum {

    SHA256withRSAPSS("SHA256withRSA/PSS", "SHA256withRSA-PSS", "RSA-PSS signature with SHA-256 digest"),
    SHA384withRSAPSS("SHA384withRSA/PSS", "SHA384withRSA-PSS", "RSA-PSS signature with SHA-384 digest"),
    SHA512withRSAPSS("SHA512withRSA/PSS", "SHA512withRSA-PSS", "RSA-PSS signature with SHA-512 digest"),
    SHA256withECDSA("SHA256withECDSA", "SHA256withECDSA", "ECDSA signature with SHA-256 digest"),
    SHA384withECDSA("SHA384withECDSA", "SHA384withECDSA", "ECDSA signature with SHA-384 digest"),
    SHA512withECDSA("SHA512withECDSA", "SHA512withECDSA", "ECDSA signature with SHA-512 digest"),
    ED25519("Ed25519", "Ed25519", "Edwards-curve DSA with Curve25519"),
    ED448("Ed448", "Ed448", "Edwards-curve DSA with Curve448");

    private static final SignatureAlgorithm[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Signature algorithm code",
            examples = {"SHA256withECDSA"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;

    SignatureAlgorithm(String code, String label, String description) {
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
    public static SignatureAlgorithm findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown signature algorithm code {}", code)));
    }
}
