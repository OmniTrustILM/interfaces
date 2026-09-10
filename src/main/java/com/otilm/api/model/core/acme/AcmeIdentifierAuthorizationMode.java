package com.otilm.api.model.core.acme;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

/**
 * What an ACME profile does with an ordered identifier its pre-authorization policy does not cover.
 */
@Schema(enumAsRef = true)
public enum AcmeIdentifierAuthorizationMode implements IPlatformEnum {
    PREAUTHORIZED_OR_CHALLENGE("preauthorizedOrChallenge", "Pre-authorized or Challenge"),
    PREAUTHORIZED_ONLY("preauthorizedOnly", "Pre-authorized Only");

    private static final AcmeIdentifierAuthorizationMode[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;

    AcmeIdentifierAuthorizationMode(String code, String label) {
        this.code = code;
        this.label = label;
    }

    @Override
    @JsonValue
    public String getCode() {
        return code;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @JsonCreator
    public static AcmeIdentifierAuthorizationMode findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown ACME Identifier Authorization Mode code {}", code)));
    }
}
