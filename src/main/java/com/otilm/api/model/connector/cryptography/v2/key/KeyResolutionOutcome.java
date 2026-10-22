package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

/** Result of resolving one key handle. */
@Schema(name = "KeyResolutionOutcome", enumAsRef = true)
public enum KeyResolutionOutcome implements IPlatformEnum {

    FOUND("found", "Found", "The token holds one key under the handle"),
    NOT_FOUND("notFound", "Not found", "The token holds no key under the handle"),
    AMBIGUOUS("ambiguous", "Ambiguous", "The token holds more than one key under the handle"),
    UNREADABLE("unreadable", "Unreadable", "The token holds the key, and the connector cannot read it"),
    INVALID_HANDLE("invalidHandle", "Invalid handle", "The handle is not one this connector issues");

    private final String code;
    private final String label;
    private final String description;

    KeyResolutionOutcome(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @JsonCreator
    public static KeyResolutionOutcome findByCode(String code) {
        return Arrays
                .stream(values())
                .filter(outcome -> outcome.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown key resolution outcome code {}", code)));
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
}
