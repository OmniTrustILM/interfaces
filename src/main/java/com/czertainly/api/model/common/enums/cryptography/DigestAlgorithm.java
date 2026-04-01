package com.czertainly.api.model.common.enums.cryptography;

import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum DigestAlgorithm implements IPlatformEnum {
    MD5("MD5","MD5", "Message Digest algorithm", "MD5", "1.2.840.113549.2.5", 16),
    SHA_1("SHA-1","SHA-1", "Secure hash algorithm 1", "SHA1", "1.3.14.3.2.26", 20),
    SHA_224("SHA-224","SHA-224", "Secure hash algorithm 2 with digest length of 224 bits", "SHA224", "2.16.840.1.101.3.4.2.4", 28),
    SHA_256("SHA-256", "SHA-256", "Secure hash algorithm 2 with digest length of 256 bits", "SHA256", "2.16.840.1.101.3.4.2.1", 32),
    SHA_384("SHA-384", "SHA-384", "Secure hash algorithm 2 with digest length of 384 bits", "SHA384", "2.16.840.1.101.3.4.2.2", 48),
    SHA_512("SHA-512", "SHA-512", "Secure hash algorithm 2 with digest length of 512 bits", "SHA512", "2.16.840.1.101.3.4.2.3", 64),
    SHA3_256("SHA3-256", "SHA3-256", "Secure hash algorithm 3 with digest length of 256 bits", "SHA3-256", "2.16.840.1.101.3.4.2.8", 32),
    SHA3_384("SHA3-384", "SHA3-384", "Secure hash algorithm 3 with digest length of 384 bits", "SHA3-384", "2.16.840.1.101.3.4.2.9", 48),
    SHA3_512("SHA3-512", "SHA3-512", "Secure hash algorithm 3 with digest length of 512 bits", "SHA3-512", "2.16.840.1.101.3.4.2.10", 64);

    private static final DigestAlgorithm[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Digest algorithm code",
            examples = {"SHA-1"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;
    private final String providerName;
    private final String oid;
    private final int digestSizeBytes;

    DigestAlgorithm(String code, String label, String description, String providerName, String oid, int digestSizeBytes) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.providerName = providerName;
        this.oid = oid;
        this.digestSizeBytes = digestSizeBytes;
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

    public String getProviderName() {
        return this.providerName;
    }

    public String getOid() {
        return this.oid;
    }

    public int getDigestSizeBytes() {
        return this.digestSizeBytes;
    }

    @JsonCreator
    public static DigestAlgorithm findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown digest algorithm code {}", code)));
    }

    public static DigestAlgorithm findByOid(String oid) {
        return Arrays.stream(VALUES)
                .filter(k -> k.oid.equals(oid))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown digest algorithm OID {}", oid)));
    }


}
