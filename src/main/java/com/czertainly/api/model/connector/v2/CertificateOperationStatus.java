package com.czertainly.api.model.connector.v2;

import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/**
 * Status of a non-synchronous certificate operation reported by an Authority Provider on
 * the status-check endpoints. Returned for an issuance, renewal, or revocation that
 * previously responded {@code 202 Accepted}.
 */
@Schema(enumAsRef = true)
public enum CertificateOperationStatus implements IPlatformEnum {

    IN_PROGRESS("inProgress", "In progress", "Operation is still being processed externally"),
    COMPLETED("completed", "Completed", "Operation finished externally; cert data included for issue/renew, no payload for revoke"),
    FAILED("failed", "Failed", "Operation failed externally; reason field carries detail");

    private static final CertificateOperationStatus[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Operation status code",
            examples = {"inProgress", "completed", "failed"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;

    CertificateOperationStatus(String code, String label, String description) {
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
    public static CertificateOperationStatus findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown certificate operation status {}", code)));
    }
}
