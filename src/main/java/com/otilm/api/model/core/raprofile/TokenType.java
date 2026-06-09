package com.otilm.api.model.core.raprofile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TokenType {
    JKS("JKS"),

    P12("P12"),

    PEM("PEM"),

    USERGENERATED("USERGENERATED");

    private final String value;

    TokenType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static TokenType fromValue(String text) {
        for (TokenType b : TokenType.values()) {
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
