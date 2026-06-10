package com.otilm.api.model.core.workflows;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;


@Schema(enumAsRef = true)
public enum ExecutionType implements IPlatformEnum {

    SET_FIELD("setField", "Set field", "Set a field of the resource"),
    SEND_NOTIFICATION("sendNotification", "Send notification", "Send a notification based on notification profile")
    ;

    private static final ExecutionType[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;

    private final String label;

    private final String description;

    ExecutionType(String code, String label, String description) {
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
    public static ExecutionType findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown execution type code {}", code)));
    }
}
