package com.otilm.api.model.core.cryptoasset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

/**
 * Type of an asset in the cross-CBOM cryptographic asset inventory. The first four values are the CycloneDX
 * {@code cryptoProperties.assetType} vocabulary, which is closed and has no value for "unknown". {@link #UNROUTABLE} is
 * the stored tier for components that declare none of the four; it is never served as an asset's {@code type}, and an
 * asset stored with it is served without one. The verdict's {@code evaluatedFields} records it as stored.
 */
@Schema(enumAsRef = true)
public enum CryptographicAssetType implements IPlatformEnum {

    ALGORITHM(Codes.ALGORITHM, "Algorithm"),
    CERTIFICATE(Codes.CERTIFICATE, "Certificate"),
    PROTOCOL(Codes.PROTOCOL, "Protocol"),
    RELATED_CRYPTO_MATERIAL(Codes.RELATED_CRYPTO_MATERIAL, "Related crypto material"),
    UNROUTABLE(Codes.UNROUTABLE, "Unroutable",
            "Stored only: a component that declares none of the CycloneDX asset types. Never served as an asset's type "
                    + "member");

    public static class Codes {
        public static final String ALGORITHM = "algorithm";
        public static final String CERTIFICATE = "certificate";
        public static final String PROTOCOL = "protocol";
        public static final String RELATED_CRYPTO_MATERIAL = "related-crypto-material";
        public static final String UNROUTABLE = "unroutable";

        private Codes() {
        }
    }

    private static final CryptographicAssetType[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    CryptographicAssetType(String code, String label) {
        this(code, label, null);
    }

    CryptographicAssetType(String code, String label, String description) {
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
    public static CryptographicAssetType findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown cryptographic asset type {}", code)));
    }
}
