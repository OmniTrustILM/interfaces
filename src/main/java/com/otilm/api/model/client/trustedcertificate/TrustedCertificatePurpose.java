package com.otilm.api.model.client.trustedcertificate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

@Schema(enumAsRef = true)
public enum TrustedCertificatePurpose implements IPlatformEnum {

    VALIDATION("validation", "Validation", "Accepted as a trust anchor when validating inventory certificates"),
    PLATFORM("platform", "Platform",
            "Trusted by the platform's services and connectors for TLS connections, and thereby accepted for "
                    + "certificate sign-in");

    private static final TrustedCertificatePurpose[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Trusted certificate purpose", examples = {"validation"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;

    TrustedCertificatePurpose(String code, String label, String description) {
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
    public static TrustedCertificatePurpose findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown trusted certificate purpose {}", code)));
    }
}
