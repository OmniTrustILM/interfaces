package com.otilm.api.model.core.signing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.List;

/**
 * How a signing operation was initiated. The {@code enableableOnProfile} flag marks which values a Signing Profile may
 * list in its enabled protocols.
 */
@Schema(enumAsRef = true,
        description = "How a signing operation was initiated. Some values name a client protocol that a Signing "
                + "Profile can enable; others record an invocation the platform makes on its own and can never be "
                + "enabled.")
public enum SigningProtocol implements IPlatformEnum {

    CSC_API(Codes.CSC_API, "CSC API Protocol", "Cloud Signature Consortium API v2", true),
    TSP(Codes.TSP, "Timestamping Protocol", "Timestamping Protocol based on RFC 3161", true),
    ILM_NATIVE(Codes.ILM_NATIVE, "ILM Native API", "ILM's own synchronous signing API for server-side integrations",
            true),
    INTERNAL_TSA(Codes.INTERNAL_TSA, "Internal TSA",
            "In-process timestamp issuance by the signing engine, recorded but never reachable by a client", false);

    private static final SigningProtocol[] VALUES;
    private static final List<SigningProtocol> ENABLEABLE_ON_PROFILE;

    static {
        VALUES = values();
        ENABLEABLE_ON_PROFILE = Arrays.stream(VALUES).filter(SigningProtocol::isEnableableOnProfile).toList();
    }

    private final String code;
    private final String label;
    private final String description;
    private final boolean enableableOnProfile;

    SigningProtocol(String code, String label, String description, boolean enableableOnProfile) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.enableableOnProfile = enableableOnProfile;
    }

    @JsonCreator
    public static SigningProtocol findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(
                        () -> new ValidationException(ValidationError.create("Unknown signing protocol {}", code)));
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

    public boolean isEnableableOnProfile() {
        return this.enableableOnProfile;
    }

    public static List<SigningProtocol> enableableValues() {
        return ENABLEABLE_ON_PROFILE;
    }

    public static class Codes {
        public static final String CSC_API = "csc_api";
        public static final String TSP = "tsp";
        public static final String ILM_NATIVE = "ilm_native";
        public static final String INTERNAL_TSA = "internal_tsa";

        private Codes() {
        }
    }
}
