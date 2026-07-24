package com.otilm.api.model.connector.cryptography.v2.token;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/**
 * Provider-observable token status for the V2 cryptography connector contract.
 */
@Schema(name = "TokenStatusV2", enumAsRef = true,
        description = "Provider-observable token status")
public enum TokenStatusV2 implements IPlatformEnum {

    CONNECTED("Connected", "Connected", "The token or its backing provider is reachable"),
    DISCONNECTED("Disconnected", "Disconnected", "The token or its backing provider is not reachable"),
    WARNING("Warning", "Warning", "The token is reachable, but a provider-observable condition requires attention"),
    UNKNOWN("Unknown", "Unknown", "The connector cannot determine the token status");

    private final String code;
    private final String label;
    private final String description;

    TokenStatusV2(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
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
        return description;
    }

    @JsonCreator
    public static TokenStatusV2 findByCode(String code) {
        return Arrays.stream(values())
                .filter(status -> status.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown V2 token status {}", code)));
    }
}
