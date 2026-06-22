package com.otilm.api.model.core.oid;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ASN.1 encoding used to encode an attribute string value into a custom X.509 extension DER value.
 * Consumers must tolerate unknown values for forward compatibility.
 */
@Schema(enumAsRef = true)
public enum ExtensionValueEncoding {

    UTF8_STRING("UTF8String"),
    IA5_STRING("IA5String"),
    PRINTABLE_STRING("PrintableString"),
    OCTET_STRING("OctetString"),
    BIT_STRING("BitString"),
    DER("DER");

    private final String code;

    ExtensionValueEncoding(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }
}
