package com.otilm.api.model.common.enums.cryptography;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.security.spec.MGF1ParameterSpec;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

/**
 * Complete RSA encryption profiles. OAEP fixes MGF1 to the message hash and fixes the label to the empty string.
 */
@Schema(enumAsRef = true,
        description = "Encryption algorithm code. RSA OAEP profiles use MGF1 with the same hash "
                + "as OAEP and an empty label. PKCS1 v1.5 and OAEP with SHA-1 are compatibility choices. "
                + "A provider offers only the profiles supported by the addressed key and its backend. ")
public enum EncryptionAlgorithm implements IPlatformEnum {
    RSA_PKCS1_V1_5("RSA/ECB/PKCS1Padding", "RSAES-PKCS1-v1_5", "RSA PKCS1 v1.5 encryption for compatibility", null),
    RSA_OAEP_SHA1("RSA/ECB/OAEPWithSHA-1AndMGF1Padding", "RSAES-OAEP with SHA-1",
            "RSA OAEP with SHA-1 and MGF1-SHA-1 for compatibility", "SHA-1"),
    RSA_OAEP_SHA256("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "RSAES-OAEP with SHA-256",
            "RSA OAEP with SHA-256 and MGF1-SHA-256", "SHA-256"),
    RSA_OAEP_SHA384("RSA/ECB/OAEPWithSHA-384AndMGF1Padding", "RSAES-OAEP with SHA-384",
            "RSA OAEP with SHA-384 and MGF1-SHA-384", "SHA-384"),
    RSA_OAEP_SHA512("RSA/ECB/OAEPWithSHA-512AndMGF1Padding", "RSAES-OAEP with SHA-512",
            "RSA OAEP with SHA-512 and MGF1-SHA-512", "SHA-512");

    private final String code;
    private final String label;
    private final String description;
    private final String oaepDigest;

    EncryptionAlgorithm(String code, String label, String description, String oaepDigest) {
        this.code = Objects.requireNonNull(code, "code must not be null");
        this.label = Objects.requireNonNull(label, "label must not be null");
        this.description = Objects.requireNonNull(description, "description must not be null");
        this.oaepDigest = oaepDigest;
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
     * Resolves explicit PKCS1 v1.5 names, including the legacy NONE mode. Bare RSA leaves padding to the provider and
     * cannot identify a profile. OAEP names alone do not identify the MGF digest or label, so they require the
     * parameter-aware overload.
     */
    public static Optional<EncryptionAlgorithm> lookupByJcaName(String cipherAlgorithm) {
        return lookupByJcaName(cipherAlgorithm, null);
    }

    /**
     * Resolves a JCA name and its effective OAEP parameters, obtained from the initialized cipher. OAEP must use the
     * named digest for both the message and MGF1, with an empty label. PKCS1 v1.5 names require null OAEP parameters.
     */
    public static Optional<EncryptionAlgorithm> lookupByJcaName(String cipherAlgorithm, OAEPParameterSpec parameters) {
        if (cipherAlgorithm == null) {
            return Optional.empty();
        }
        String normalized = normalizeJcaName(cipherAlgorithm);
        return Arrays
                .stream(values())
                .filter(algorithm -> normalizeJcaName(algorithm.code).equals(normalized))
                .filter(algorithm -> algorithm.oaepDigest == null
                        ? parameters == null
                        : algorithm.matchesOaepParameters(parameters))
                .findFirst();
    }

    private boolean matchesOaepParameters(OAEPParameterSpec parameters) {
        return parameters != null && "MGF1".equalsIgnoreCase(parameters.getMGFAlgorithm())
                && parameters.getMGFParameters() instanceof MGF1ParameterSpec mgf
                && parameters.getPSource() instanceof PSource.PSpecified label && label.getValue().length == 0
                && normalizeJcaName(oaepDigest).equals(normalizeJcaName(parameters.getDigestAlgorithm()))
                && normalizeJcaName(oaepDigest).equals(normalizeJcaName(mgf.getDigestAlgorithm()));
    }

    private static String normalizeJcaName(String cipherAlgorithm) {
        return cipherAlgorithm.toUpperCase(Locale.ROOT).replace("/NONE/", "/ECB/").replace("SHA-", "SHA");
    }
}
