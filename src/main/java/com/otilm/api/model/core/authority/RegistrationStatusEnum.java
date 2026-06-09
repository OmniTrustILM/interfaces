package com.otilm.api.model.core.authority;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RegistrationStatusEnum {
    NEW("NEW"),

    ANY("ANY");

    private final String value;

    RegistrationStatusEnum(String value) {
        this.value = value;
    }

    @JsonCreator
    public static RegistrationStatusEnum fromValue(String text) {
        for (RegistrationStatusEnum b : RegistrationStatusEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }
}
