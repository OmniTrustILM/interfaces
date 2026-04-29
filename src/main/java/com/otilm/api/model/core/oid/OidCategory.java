package com.otilm.api.model.core.oid;

import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum OidCategory implements IPlatformEnum {

    RDN_ATTRIBUTE_TYPE(Codes.RDN_ATTRIBUTE_TYPE, "RDN Attribute Type", "OID for a type of attribute that can appear in DN"),
    EXTENDED_KEY_USAGE(Codes.EXTENDED_KEY_USAGE, "Extended Key Usage", "OID specifying key purpose in Extended Key Usage extension"),
    GENERIC(Codes.GENERIC, "Generic", "Generic OID for general use")
    ;

    private static final OidCategory[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    OidCategory(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
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
        public static final String RDN_ATTRIBUTE_TYPE = "rdnAttributeType";
        public static final String EXTENDED_KEY_USAGE = "extendedKeyUsage";
        public static final String GENERIC = "generic";

        private Codes() {

        }
    }
}
