package com.czertainly.api.model.client.signing.profile.scheme;

import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum ManagedSigningType implements IPlatformEnum {

    /**
     * The signing certificate and key pair are provisioned once (e.g. via Token Profile) and reused
     * across all signing operations that reference this Signing Profile. The key persists on the
     * cryptographic token for the lifetime of the profile.
     */
    STATIC_KEY(Codes.STATIC_KEY, "Static Key", "Signing uses a pre-existing static certificate and key pair"),

    /**
     * A fresh certificate and key pair is issued by the configured CA for each individual signing
     * operation. The newly issued certificate is valid only for that operation and is not reused.
     * Requires an RA Profile to be configured on the Signing Profile.
     */
    ONE_TIME_KEY(Codes.ONE_TIME_KEY, "One-Time Key", "Signing uses a freshly issued certificate and key pair per operation");

    private static final ManagedSigningType[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    ManagedSigningType(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @JsonCreator
    public static ManagedSigningType findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown managed signing type {}", code)));
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

    public static class Codes {
        public static final String STATIC_KEY = "staticKey";
        public static final String ONE_TIME_KEY = "oneTimeKey";

        private Codes() {
        }
    }
}
