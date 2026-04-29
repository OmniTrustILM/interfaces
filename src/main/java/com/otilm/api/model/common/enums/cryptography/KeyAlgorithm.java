package com.otilm.api.model.common.enums.cryptography;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum KeyAlgorithm implements IPlatformEnum {

    RSA("RSA", "RSA", "Rivest–Shamir–Adleman"),
    ECDSA("ECDSA", "ECDSA", "Elliptic Curve Digital Signature Algorithm"),
    FALCON("FALCON", "FALCON", "Fast Fourier lattice-based compact signatures over NTRU"),
    MLDSA("ML-DSA", "ML-DSA", "Post-quantum Module-Lattice-Based digital signature algorithm standardized by NIST, also known as CRYSTALS-Dilithium"),
    SLHDSA("SLH-DSA", "SLH-DSA", "Post-quantum stateless hash-based digital signature scheme standardized by NIST, also known as SPHINCS+"),
    MLKEM("ML-KEM", "ML-KEM", "Post-quantum Module-Lattice-Based Key-Encapsulation mechanism and the primary KEM standardized by NIST, also known as CRYSTALS-Kyber"),
    @Deprecated DILITHIUM("CRYSTALS-Dilithium", "CRYSTALS-Dilithium", "Post-quantum lattice-based signature scheme"),
    @Deprecated SPHINCSPLUS("SPHINCS+", "SPHINCS+", "Post-quantum stateless hash-based signature scheme"),
    UNKNOWN("Unknown", "Unknown", "Key algorithm not recognized");

    private static final KeyAlgorithm[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Cryptographic algorithm code",
            examples = {"RSA"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;

    KeyAlgorithm(String code, String label, String description) {
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
    public static KeyAlgorithm findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown cryptographic algorithm code {}", code)));
    }
}
