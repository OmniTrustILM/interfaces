package com.czertainly.api.model.core.signing;

import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum SigningProtocol implements IPlatformEnum {

    CSC_API(Codes.CSC_API, "CSC API Protocol", "Cloud Signature Consortium API v2"),
    ILM_SIGNING_PROTOCOL(Codes.ILM_SIGNING_PROTOCOL, "ILM Signing Protocol", "internal ILM-based signing protocol"),
    TSP(Codes.TSP, "Timestamping Protocol", "Timestamping Protocol based on RFC 3161"),
    ;

    private static final SigningProtocol[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    SigningProtocol(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @JsonCreator
    public static SigningProtocol findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown signing protocol {}", code)));
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
        public static final String CSC_API = "csc_api";
        public static final String ILM_SIGNING_PROTOCOL = "ilm_signing_protocol";
        public static final String TSP = "tsp";

        private Codes() {
        }
    }
}
