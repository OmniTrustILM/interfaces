package com.otilm.api.model.core.acme;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * List of possible status for the Authorization object in ACME protocol
 */
public enum AuthorizationStatus {
    PENDING("pending"),
    VALID("valid"),
    INVALID("invalid"),
    DEACTIVATED("deactivated"),
    EXPIRED("expired"),
    REVOKED("revoked");

    /**
     * Status code for any given status
     */
    private final String code;

    AuthorizationStatus(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return this.code;
    }

    @JsonCreator
    public static AuthorizationStatus findByCode(String code) {
        return Arrays.stream(AuthorizationStatus.values())
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown ACME authorization status code {}", code)));
    }
}
