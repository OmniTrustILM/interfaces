package com.otilm.api.model.core.est;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

/**
 * How an EST Profile authenticates clients that enroll or ask for a server-generated key. A profile selects exactly one
 * policy and never falls back to another.
 */
@Schema(enumAsRef = true)
public enum EstAuthenticationPolicy implements IPlatformEnum {
    CERTIFICATE_REGISTRATION(Codes.CERTIFICATE_REGISTRATION, "Certificate Registration",
            "HTTP Basic with a pre-registered certificate's authorization secret"),
    RA_BASIC(Codes.RA_BASIC, "RA Basic Credential",
            "HTTP Basic checked against Secret-backed RA credentials; a Secret counts only when its source vault "
                    + "profile is enabled"),
    VENDOR_MTLS(Codes.VENDOR_MTLS, "Vendor Certificate",
            "Client certificate from a trusted vendor CA; the request keeps that certificate's identity"),
    RA_MTLS(Codes.RA_MTLS, "RA Certificate", "Client certificate from a trusted CA carrying the RA extended key usage");

    private static final EstAuthenticationPolicy[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    EstAuthenticationPolicy(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @JsonCreator
    public static EstAuthenticationPolicy findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown EST authentication policy {}", code)));
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
        public static final String CERTIFICATE_REGISTRATION = "certificateRegistration";
        public static final String RA_BASIC = "raBasic";
        public static final String VENDOR_MTLS = "vendorMtls";
        public static final String RA_MTLS = "raMtls";

        private Codes() {
        }
    }
}
