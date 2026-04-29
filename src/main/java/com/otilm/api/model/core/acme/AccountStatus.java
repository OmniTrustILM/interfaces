package com.otilm.api.model.core.acme;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/**
 * Mapping of the ENUM to the possible values of the status of ACME Account
 */
@Schema(enumAsRef = true)
public enum AccountStatus implements IPlatformEnum {

    VALID("valid", "Valid"),
    DEACTIVATED("deactivated", "Deactivated"),
    REVOKED("revoked", "Revoked");

    private static final AccountStatus[] VALUES;

    static {
        VALUES = values();
    }

    /**
     * Status code for any given status
     */
    private final String code;
    private final String label;
    private final String description;

    AccountStatus(String code, String label) {
        this(code, label,null);
    }

    AccountStatus(String code, String label, String description) {
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
        return this.description;
    }

    @JsonCreator
    public static AccountStatus findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown ACME Account status code {}", code)));
    }
}
