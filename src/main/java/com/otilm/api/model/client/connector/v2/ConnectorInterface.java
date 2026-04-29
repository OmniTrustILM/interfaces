package com.otilm.api.model.client.connector.v2;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;


@Schema(enumAsRef = true)
public enum ConnectorInterface implements IPlatformEnum {

    INFO("info", "Info"),
    HEALTH("health", "Health"),
    METRICS("metrics", "Metrics"),
    AUTHORITY("authority", "Authority"),
    DISCOVERY("discovery", "Discovery"),
    ENTITY("entity", "Entity"),
    COMPLIANCE("compliance", "Compliance"),
    CRYPTOGRAPHY("cryptography", "Cryptography"),
    NOTIFICATION("notification", "Notification"),
    SECRET("secret", "Secret");

    private static final ConnectorInterface[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Connector interface code",
            examples = {"credentialProvider"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;

    ConnectorInterface(String code, String label) {
        this(code, label, null);
    }

    ConnectorInterface(String code, String label, String description) {
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
    public static ConnectorInterface findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown connector interface code {}", code)));
    }
}
