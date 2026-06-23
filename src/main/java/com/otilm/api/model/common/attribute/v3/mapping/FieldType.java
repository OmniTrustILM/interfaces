package com.otilm.api.model.common.attribute.v3.mapping;

import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum FieldType implements IPlatformEnum {

    // X.509 certificate
    RDN(Codes.RDN, "RDN"),
    SAN(Codes.SAN, "Subject Alternative Name"),
    EXTENSION(Codes.EXTENSION, "Extension");

    public static class Codes {
        public static final String RDN = "rdn";
        public static final String SAN = "san";
        public static final String EXTENSION = "extension";

        private Codes() {}
    }

    private static final FieldType[] VALUES = values();

    private final String code;
    private final String label;

    FieldType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    @Override
    @JsonValue
    public String getCode() { return code; }

    @Override
    public String getLabel() { return label; }

    @Override
    public String getDescription() { return null; }

    @JsonCreator
    public static FieldType fromCode(String code) {
        return Arrays.stream(VALUES).filter(v -> v.code.equals(code)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown FieldType code: " + code));
    }
}
