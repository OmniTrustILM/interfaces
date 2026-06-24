package com.otilm.api.model.core.certificate;

import com.otilm.api.model.common.enums.IPlatformEnum;
import com.otilm.api.model.core.oid.ExtensionValueEncoding;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum GeneralNameType implements IPlatformEnum {

    /** RFC 5280: dNSName [2] IA5String */
    DNS("dns", "DNS Name", ExtensionValueEncoding.IA5_STRING),
    /** RFC 5280: rfc822Name [1] IA5String */
    EMAIL("email", "Email (RFC 822)", ExtensionValueEncoding.IA5_STRING),
    /** RFC 5280: iPAddress [7] OCTET STRING */
    IP("ip", "IP Address", ExtensionValueEncoding.OCTET_STRING),
    /** RFC 5280: uniformResourceIdentifier [6] IA5String */
    URI("uri", "URI", ExtensionValueEncoding.IA5_STRING),
    /** RFC 5280: otherName [0] OtherName; value string is UTF-8 encoded */
    OTHER_NAME("otherName", "Other Name", ExtensionValueEncoding.UTF8_STRING),
    /** RFC 5280: directoryName [4] Name; DER-encoded */
    DIRECTORY_NAME("directoryName", "Directory Name", ExtensionValueEncoding.DER),
    /** RFC 5280: registeredID [8] OBJECT IDENTIFIER; no value encoding applies */
    REGISTERED_ID("registeredId", "Registered ID (OID)", null);

    private static final GeneralNameType[] VALUES = values();

    private final String code;
    private final String label;
    @Getter
    private final ExtensionValueEncoding encoding;

    GeneralNameType(String code, String label, ExtensionValueEncoding encoding) {
        this.code = code;
        this.label = label;
        this.encoding = encoding;
    }

    @Override
    @JsonValue
    public String getCode() { return code; }

    @Override
    public String getLabel() { return label; }

    @Override
    public String getDescription() { return null; }

    @JsonCreator
    public static GeneralNameType fromCode(String code) {
        return Arrays.stream(VALUES)
                .filter(v -> v.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown GeneralNameType code: " + code));
    }
}
