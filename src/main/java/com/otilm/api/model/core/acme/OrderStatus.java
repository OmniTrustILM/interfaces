package com.otilm.api.model.core.acme;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum OrderStatus {
    PENDING("pending"),
    READY("ready"),
    PROCESSING("processing"),
    VALID("valid"),
    INVALID("invalid");

    /**
     * Status code for any given status
     */
    private final String code;

    OrderStatus(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return this.code;
    }

    @JsonCreator
    public static OrderStatus findByCode(String code) {
        return Arrays.stream(OrderStatus.values())
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown ACME Order status code {}", code)));
    }
}
