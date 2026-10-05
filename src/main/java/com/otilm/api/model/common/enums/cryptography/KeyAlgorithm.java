package com.otilm.api.model.common.enums.cryptography;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

@Schema(enumAsRef = true)
public enum KeyAlgorithm implements IPlatformEnum {

    RSA("RSA", "RSA", "Rivest–Shamir–Adleman"),
    ECDSA("ECDSA", "ECDSA", "Elliptic Curve Digital Signature Algorithm"),
    FALCON("FALCON", "FALCON", "Fast Fourier lattice-based compact signatures over NTRU"),
    MLDSA("ML-DSA", "ML-DSA",
            "Post-quantum Module-Lattice-Based digital signature algorithm standardized by NIST, also known as CRYSTALS-Dilithium"),
    SLHDSA("SLH-DSA", "SLH-DSA",
            "Post-quantum stateless hash-based digital signature scheme standardized by NIST, also known as SPHINCS+"),
    MLKEM("ML-KEM", "ML-KEM",
            "Post-quantum Module-Lattice-Based Key-Encapsulation mechanism and the primary KEM standardized by NIST, also known as CRYSTALS-Kyber"),
    @Deprecated
    DILITHIUM("CRYSTALS-Dilithium", "CRYSTALS-Dilithium", "Post-quantum lattice-based signature scheme"),
    @Deprecated
    SPHINCSPLUS("SPHINCS+", "SPHINCS+", "Post-quantum stateless hash-based signature scheme"),
    AES("AES", "AES", "Advanced Encryption Standard", false),
    UNKNOWN("Unknown", "Unknown", "Key algorithm not recognized", false);

    private static final KeyAlgorithm[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Cryptographic algorithm code", examples = {"RSA"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;
    private final boolean keyPairAlgorithm;

    KeyAlgorithm(String code, String label, String description) {
        this(code, label, description, true);
    }

    KeyAlgorithm(String code, String label, String description, boolean keyPairAlgorithm) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.keyPairAlgorithm = keyPairAlgorithm;
    }

    /**
     * Whether this algorithm produces a key pair rather than a single secret key: {@code false} for a secret-key
     * algorithm such as {@link #AES}, and for {@link #UNKNOWN}. It exists so a rule pairing a key type with an
     * algorithm stays correct as the algorithms it recognizes grow.
     *
     * @return whether the algorithm is used for key pairs
     */
    public boolean isKeyPairAlgorithm() {
        return keyPairAlgorithm;
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
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown cryptographic algorithm code {}", code)));
    }
}
