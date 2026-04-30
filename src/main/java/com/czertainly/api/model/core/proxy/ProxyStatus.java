package com.czertainly.api.model.core.proxy;

import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum ProxyStatus implements IPlatformEnum {

    INITIALIZED("initialized", "Initial state"),
    PROVISIONING("provisioning", "Provisioning in progress"),
    FAILED("failed", "Failed"),
    WAITING_FOR_INSTALLATION("waitingForInstallation", "Waiting for installation"),
    CONNECTED("connected", "Connected"),
    DISCONNECTED("disconnected", "Disconnected")
    ;

    private static final ProxyStatus[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Proxy status",
            examples = {"connected"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;

    ProxyStatus(String code, String label) {
        this(code, label,null);
    }

    ProxyStatus(String code, String label, String description) {
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
    public static ProxyStatus findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown Proxy status {}", code)));
    }
}
