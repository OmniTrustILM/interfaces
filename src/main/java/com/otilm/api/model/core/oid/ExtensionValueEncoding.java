package com.otilm.api.model.core.oid;

import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum ExtensionValueEncoding implements IPlatformEnum {

    UTF8_STRING("UTF8String", "UTF-8 String"),
    IA5_STRING("IA5String", "IA5 String"),
    PRINTABLE_STRING("PrintableString", "Printable String"),
    OCTET_STRING("OctetString", "Octet String"),
    BIT_STRING("BitString", "Bit String"),
    DER("DER", "DER (Base64-encoded)");

    private static final ExtensionValueEncoding[] VALUES = values();

    private final String code;
    private final String label;

    ExtensionValueEncoding(String code, String label) {
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

    public static ExtensionValueEncoding findByCode(String code) {
        return Arrays.stream(VALUES).filter(v -> v.code.equals(code)).findFirst().orElse(null);
    }

    @JsonCreator
    public static ExtensionValueEncoding fromCode(String code) { return findByCode(code); }
}
