package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

/** Connector's admission or lookup result for a key, as a listing or a resolution reports it. */
@Schema(name = "KeyEntryStatus", enumAsRef = true)
public enum KeyEntryStatus implements IPlatformEnum {

    AVAILABLE("available", "Available", "The connector offers the key for onboarding"),
    REFUSED("refused", "Refused", "The connector can read the key and refuses to let the platform onboard it"),
    UNADDRESSABLE("unaddressable", "Unaddressable",
            "The connector cannot identify the key uniquely by a handle, or the handle matches more than one key"),
    UNSUPPORTED_TYPE("unsupportedType", "Unsupported type", "The key is of a type the connector cannot operate"),
    UNREADABLE("unreadable", "Unreadable", "The connector cannot read the key"),
    NOT_FOUND("notFound", "Not found", "The token holds no matching key"),
    INVALID_HANDLE("invalidHandle", "Invalid handle", "The handle is not one this connector issues");

    private final String code;
    private final String label;
    private final String description;

    KeyEntryStatus(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @JsonCreator
    public static KeyEntryStatus findByCode(String code) {
        return Arrays
                .stream(values())
                .filter(status -> status.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown key entry status code {}", code)));
    }

    public boolean carriesListedKey() {
        return switch (this) {
            case AVAILABLE, REFUSED -> true;
            case UNADDRESSABLE, UNSUPPORTED_TYPE, UNREADABLE, NOT_FOUND, INVALID_HANDLE -> false;
        };
    }

    public boolean matchesKey() {
        return switch (this) {
            case AVAILABLE, REFUSED, UNADDRESSABLE, UNSUPPORTED_TYPE, UNREADABLE -> true;
            case NOT_FOUND, INVALID_HANDLE -> false;
        };
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
