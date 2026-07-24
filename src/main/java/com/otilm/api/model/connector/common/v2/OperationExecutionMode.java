package com.otilm.api.model.connector.common.v2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/**
 * Caller-selected completion mode for operations that support asynchronous execution.
 */
@Schema(name = "OperationExecutionMode", enumAsRef = true,
        description = "Whether the connector must complete the operation inline or accept it for asynchronous processing")
public enum OperationExecutionMode implements IPlatformEnum {

    SYNCHRONOUS("synchronous", "Synchronous", "Complete the operation before returning the response"),
    ASYNCHRONOUS("asynchronous", "Asynchronous", "Return 202 Accepted with an operation tracking handle");

    private final String code;
    private final String label;
    private final String description;

    OperationExecutionMode(String code, String label, String description) {
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
    public static OperationExecutionMode findByCode(String code) {
        return Arrays.stream(values())
                .filter(mode -> mode.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown cryptography operation execution mode {}", code)));
    }
}
