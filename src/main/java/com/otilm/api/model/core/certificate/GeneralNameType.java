package com.otilm.api.model.core.certificate;

import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum GeneralNameType implements IPlatformEnum {

    DNS("dns", "DNS Name"),
    EMAIL("email", "Email (RFC 822)"),
    IP("ip", "IP Address"),
    URI("uri", "URI"),
    OTHER_NAME("otherName", "Other Name"),
    DIRECTORY_NAME("directoryName", "Directory Name"),
    REGISTERED_ID("registeredId", "Registered ID (OID)");

    private static final GeneralNameType[] VALUES = values();

    private final String code;
    private final String label;

    GeneralNameType(String code, String label) {
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

    public static GeneralNameType findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(v -> v.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown GeneralNameType code: " + code));
    }

    @JsonCreator
    public static GeneralNameType fromCode(String code) {
        return findByCode(code);
    }
}
