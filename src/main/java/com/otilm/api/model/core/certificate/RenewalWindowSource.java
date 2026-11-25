package com.otilm.api.model.core.certificate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

@Schema(enumAsRef = true)
public enum RenewalWindowSource implements IPlatformEnum {

    POLICY("policy", "Policy", "Window computed from the RA profile renewal settings"),
    AUTHORITY("authority", "Certificate authority", "Window suggested by the certificate authority"),
    FORCED("forced", "Forced", "Window forced to start immediately");

    private static final RenewalWindowSource[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;

    private final String label;

    private final String description;

    RenewalWindowSource(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
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

    @JsonCreator
    public static RenewalWindowSource findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown renewal window source code {}", code)));
    }
}
