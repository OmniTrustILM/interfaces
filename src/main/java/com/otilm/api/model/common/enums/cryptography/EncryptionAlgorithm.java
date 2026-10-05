package com.otilm.api.model.common.enums.cryptography;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Complete RSA encryption profiles. OAEP fixes MGF1 to the message hash and fixes the label to the empty string.
 */
@Schema(enumAsRef = true,
        description = "Encryption algorithm code. RSA OAEP profiles use MGF1 with the same hash "
                + "as OAEP and an empty label. PKCS1 v1.5 and OAEP with SHA-1 are compatibility choices. "
                + "A provider offers only the profiles supported by the addressed key and its backend.")
public enum EncryptionAlgorithm implements IPlatformEnum {
    RSA_PKCS1_V1_5("RSA/ECB/PKCS1Padding", "RSAES-PKCS1-v1_5", "RSA PKCS1 v1.5 encryption for compatibility"),
    RSA_OAEP_SHA1("RSA/ECB/OAEPWithSHA-1AndMGF1Padding", "RSAES-OAEP with SHA-1",
            "RSA OAEP with SHA-1 and MGF1-SHA-1 for compatibility"),
    RSA_OAEP_SHA256("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "RSAES-OAEP with SHA-256",
            "RSA OAEP with SHA-256 and MGF1-SHA-256"),
    RSA_OAEP_SHA384("RSA/ECB/OAEPWithSHA-384AndMGF1Padding", "RSAES-OAEP with SHA-384",
            "RSA OAEP with SHA-384 and MGF1-SHA-384"),
    RSA_OAEP_SHA512("RSA/ECB/OAEPWithSHA-512AndMGF1Padding", "RSAES-OAEP with SHA-512",
            "RSA OAEP with SHA-512 and MGF1-SHA-512");

    private final String code;
    private final String label;
    private final String description;

    EncryptionAlgorithm(String code, String label, String description) {
        this.code = Objects.requireNonNull(code, "code must not be null");
        this.label = Objects.requireNonNull(label, "label must not be null");
        this.description = Objects.requireNonNull(description, "description must not be null");
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
        return description;
    }

    public static Optional<EncryptionAlgorithm> lookupByCode(String code) {
        return Arrays.stream(values()).filter(algorithm -> algorithm.code.equalsIgnoreCase(code)).findFirst();
    }

    @JsonCreator
    public static EncryptionAlgorithm findByCode(String code) {
        return lookupByCode(code)
                .orElseThrow(
                        () -> new ValidationException(ValidationError.create("Unknown encryption algorithm code.")));
    }

    /**
     * Maps explicit JCA names to profiles. Accepts NONE mode and digest aliases. OAEP assumes matching OAEP/MGF1
     * digests and empty label. Provider defaults ignored. Bare RSA is unsupported.
     */
    public static Optional<EncryptionAlgorithm> lookupByJcaName(String cipherAlgorithm) {
        if (cipherAlgorithm == null) {
            return Optional.empty();
        }
        String normalized = normalizeJcaName(cipherAlgorithm);
        return Arrays
                .stream(values())
                .filter(algorithm -> normalizeJcaName(algorithm.code).equals(normalized))
                .findFirst();
    }

    private static String normalizeJcaName(String cipherAlgorithm) {
        return cipherAlgorithm.toUpperCase(Locale.ROOT).replace("/NONE/", "/ECB/").replace("SHA-", "SHA");
    }
}
