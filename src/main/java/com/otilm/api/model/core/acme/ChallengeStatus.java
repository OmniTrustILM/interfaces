package com.otilm.api.model.core.acme;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * ENUM representing the possible values for the ACME challenge
 */
public enum ChallengeStatus {
    PENDING("pending"),
    VALID("valid"),
    INVALID("invalid"),
    PROCESSING("processing");

    /**
     * Status code for any given status
     */
    private final String code;

    ChallengeStatus(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return this.code;
    }

    @JsonCreator
    public static ChallengeStatus findByCode(String code) {
        return Arrays.stream(ChallengeStatus.values())
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown ACME Challenge status code {}", code)));
    }
}
