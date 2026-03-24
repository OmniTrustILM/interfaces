package com.czertainly.api.model.core.signing.signatureprofile;

import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum SigningScheme implements IPlatformEnum {

    MANAGED(Codes.MANAGED, "Managed Signing", "ILM manages the signing workflow"),
    DELEGATED(Codes.DELEGATED, "Delegated Signing", "ILM delegates the signing to an external signing service")
    ;

    private static final SigningScheme[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    SigningScheme(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @JsonCreator
    public static SigningScheme findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown signing scheme {}", code)));
    }

    @Override
    @JsonValue
    public String getCode() {
        return this.code;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public static class Codes {
        public static final String MANAGED = "managed";
        public static final String DELEGATED = "delegated";

        private Codes() {
        }
    }
}
