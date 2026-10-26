package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

/** Why the platform cannot onboard a key the token holds. */
@Schema(name = "UnusableKeyReason", enumAsRef = true)
public enum UnusableKeyReason implements IPlatformEnum {

    UNADDRESSABLE("unaddressable", "Unaddressable",
            "The connector cannot name the key in a handle that addresses it alone"),
    UNSUPPORTED_TYPE("unsupportedType", "Unsupported type", "The key is of a type the connector cannot operate"),
    UNREADABLE("unreadable", "Unreadable", "The connector cannot read the key"),
    NOT_FOUND("notFound", "Not found", "The token no longer holds the key it counted");

    private final String code;
    private final String label;
    private final String description;

    UnusableKeyReason(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @JsonCreator
    public static UnusableKeyReason findByCode(String code) {
        return Arrays
                .stream(values())
                .filter(reason -> reason.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown unusable key reason code {}", code)));
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
