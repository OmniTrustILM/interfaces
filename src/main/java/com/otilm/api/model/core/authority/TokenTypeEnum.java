package com.otilm.api.model.core.authority;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TokenTypeEnum {
    JKS("JKS"),

    P12("P12"),

    PEM("PEM"),

    USERGENERATED("USERGENERATED");

    private final String value;

    TokenTypeEnum(String value) {
        this.value = value;
    }

    @JsonCreator
    public static TokenTypeEnum fromValue(String text) {
        for (TokenTypeEnum b : TokenTypeEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }
}
