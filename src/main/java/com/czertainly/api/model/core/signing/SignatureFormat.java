package com.czertainly.api.model.core.signing;

import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum SignatureFormat implements IPlatformEnum {

    CADES(Codes.CADES, "CAdES", "CMS Advanced Electronic Signatures (ETSI EN 319 122)"),
    XADES(Codes.XADES, "XAdES", "XML Advanced Electronic Signatures (ETSI EN 319 132)"),
    PADES(Codes.PADES, "PAdES", "PDF Advanced Electronic Signatures (ETSI EN 319 102)"),
    JADES(Codes.JADES, "JAdES", "JSON Advanced Electronic Signatures (ETSI TS 119 182)"),
    RAW(Codes.RAW, "Raw", "Raw cryptographic signature value without any encapsulation format"),
    ;

    private static final SignatureFormat[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    SignatureFormat(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @JsonCreator
    public static SignatureFormat findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown signature format {}", code)));
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
        public static final String CADES = "CAdES";
        public static final String XADES = "XAdES";
        public static final String PADES = "PAdES";
        public static final String JADES = "JAdES";
        public static final String RAW = "RAW";

        private Codes() {
        }
    }
}
