package com.otilm.api.model.core.connector;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum ConnectorStatus implements IPlatformEnum {

    WAITING_FOR_APPROVAL("waitingForApproval", "Waiting for approval"),
    CONNECTED("connected", "Connected"),
    FAILED("failed", "Failed"),
    OFFLINE("offline", "Offline")
    ;

    private static final ConnectorStatus[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Connector status",
            examples = {"connected"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;

    ConnectorStatus(String code, String label) {
        this(code, label,null);
    }

    ConnectorStatus(String code, String label, String description) {
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
    public static ConnectorStatus findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown Connector status {}", code)));
    }
}
