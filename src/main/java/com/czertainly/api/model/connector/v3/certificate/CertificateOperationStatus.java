package com.czertainly.api.model.connector.v3.certificate;

import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum CertificateOperationStatus implements IPlatformEnum {

    IN_PROGRESS("inProgress", "In progress", "Operation is still running at the upstream CA"),
    COMPLETED("completed", "Completed", "Operation has reached terminal success"),
    FAILED("failed", "Failed", "Operation has reached terminal failure");

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

    @JsonCreator
    public static CertificateOperationStatus findByCode(String code) {
        return Arrays.stream(values())
                .filter(s -> s.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown certificate operation status code {}", code)));
    }
}
