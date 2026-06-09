package com.otilm.api.model.core.cmp;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum CmpProfileVariant implements IPlatformEnum {
    //V1("v1", "CMPv1", "Implementation of RFC 2510 (CMP version 1)"),
    V2("v2", "CMPv2", "Implementation of RFC 4210 and RFC 4211 (CMP version 2)"),
    V2_3GPP("v2_3gpp", "CMPv2 3GPP", "3GPP implementation based on CMPv2"),
    V3("v3", "CMPv3", "Implementation of RFC 9483 (lightweight cmp protocol, CMP version 3)"),;

    private final String code;
    private final String label;
    private final String description;

    CmpProfileVariant(String code, String label) {
        this(code, label,null);
    }

    CmpProfileVariant(String code, String label, String description) {
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

    private static final CmpProfileVariant[] VALUES;

    static {
        VALUES = values();
    }

    @JsonCreator
    public static CmpProfileVariant findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown cmp-transaction state {}", code)));
    }
}
