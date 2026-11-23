package com.otilm.api.model.client.trustedcertificate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

@Schema(enumAsRef = true)
public enum TrustedCertificatePlatformState implements IPlatformEnum {

    PENDING("pending", "Pending", "Stored, not yet applied by the platform"),
    APPLYING("applying", "Applying", "A deployment carrying it is running"),
    APPLIED("applied", "Applied", "The platform carries it"),
    FAILED("failed", "Failed", "The last deployment carrying it failed"),
    REMOVING("removing", "Removing", "Removal requested, the deployment dropping it has not landed yet"),
    REMOVAL_FAILED("removalFailed", "Removal Failed", "The deployment that should have dropped it failed"),
    MISSING("missing", "Missing on Platform", "Trusted by ILM, but the platform no longer holds it"),
    UNKNOWN("unknown", "Unknown", "The platform has not been reachable since the change, or reports no state");

    private static final TrustedCertificatePlatformState[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Trusted certificate platform state", examples = {"applied"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;

    TrustedCertificatePlatformState(String code, String label) {
        this(code, label, null);
    }

    TrustedCertificatePlatformState(String code, String label, String description) {
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
    public static TrustedCertificatePlatformState findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown trusted certificate platform state {}", code)));
    }
}
