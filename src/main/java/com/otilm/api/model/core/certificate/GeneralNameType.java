package com.otilm.api.model.core.certificate;

import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.bouncycastle.asn1.x509.GeneralName;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum GeneralNameType implements IPlatformEnum {

    /** RFC 5280: dNSName [2] IA5String */
    DNS("dns", "DNS Name", GeneralName.dNSName),
    /** RFC 5280: rfc822Name [1] IA5String */
    EMAIL("email", "Email (RFC 822)", GeneralName.rfc822Name),
    /** RFC 5280: iPAddress [7] OCTET STRING */
    IP("ip", "IP Address", GeneralName.iPAddress),
    /** RFC 5280: uniformResourceIdentifier [6] IA5String */
    URI("uri", "URI", GeneralName.uniformResourceIdentifier),
    /** RFC 5280: otherName [0] OtherName */
    OTHER_NAME("otherName", "Other Name", GeneralName.otherName),
    /** RFC 5280: directoryName [4] Name */
    DIRECTORY_NAME("directoryName", "Directory Name", GeneralName.directoryName),
    /** RFC 5280: registeredID [8] OBJECT IDENTIFIER */
    REGISTERED_ID("registeredId", "Registered ID (OID)", GeneralName.registeredID);

    private static final GeneralNameType[] VALUES = values();

    private final String code;
    private final String label;
    @Getter
    private final int bcTag;

    GeneralNameType(String code, String label, int bcTag) {
        this.code = code;
        this.label = label;
        this.bcTag = bcTag;
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
