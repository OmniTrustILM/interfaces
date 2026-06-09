package com.otilm.api.model.core.acme;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * ENUM representing the possible values for the ACME Challenge type
 */
public enum ChallengeType {
    HTTP01("http-01"),
    DNS01("dns-01");

    /**
     * Type of Challenge code
     */
    private final String code;

    ChallengeType(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return this.code;
    }

    @JsonCreator
    public static ChallengeType findByCode(String code) {
        return Arrays.stream(ChallengeType.values())
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown ACME Challenge Type {}", code)));
    }
}
