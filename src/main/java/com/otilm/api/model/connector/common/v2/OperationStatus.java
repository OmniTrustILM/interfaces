package com.otilm.api.model.connector.common.v2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/**
 * Terminal/non-terminal state of an asynchronous connector operation.
 *
 * <p>Shared by connector interfaces that expose the 202 + /status + /cancel pattern.</p>
 */
@Schema(name = "OperationStatus", enumAsRef = true)
public enum OperationStatus implements IPlatformEnum {

    IN_PROGRESS("inProgress", "In progress", "Operation is still running at the upstream system"),
    COMPLETED("completed", "Completed", "Operation has reached terminal success"),
    FAILED("failed", "Failed", "Operation has reached terminal failure");

    private final String code;
    private final String label;
    private final String description;

    OperationStatus(String code, String label, String description) {
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
    public static OperationStatus findByCode(String code) {
        return Arrays.stream(values())
                .filter(s -> s.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown operation status code {}", code)));
    }
}
