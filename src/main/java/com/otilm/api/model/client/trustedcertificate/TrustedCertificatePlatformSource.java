package com.otilm.api.model.client.trustedcertificate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

@Schema(enumAsRef = true)
public enum TrustedCertificatePlatformSource implements IPlatformEnum {

    PROVISIONING("provisioning", "Provisioning", "Platform trust is managed from ILM"),
    DEPLOYMENT("deployment", "Deployment", "Platform trust is the read-only bundle of the deployment"),
    NONE("none", "None", "No platform trust source is configured");

    private static final TrustedCertificatePlatformSource[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Trusted certificate platform source", examples = {"provisioning"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;

    TrustedCertificatePlatformSource(String code, String label, String description) {
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
    public static TrustedCertificatePlatformSource findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown trusted certificate platform source {}", code)));
    }
}
