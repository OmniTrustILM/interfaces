package com.czertainly.api.model.core.logging.enums;

import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum Module implements IPlatformEnum {
    APPROVALS("approvals", "Approvals"),
    AUTH("auth", "Auth", "Authentication and authorization module"),
    CERTIFICATES("certificates", "Certificates", "Certificates management and operations"),
    CRYPTOGRAPHIC_KEYS("keys", "Cryptographic keys", "Cryptographic keys management and operations"),
    COMPLIANCE("compliance", "Compliance"),
    CORE("core", "Core", "Core functionality including connectors, credentials and attributes"),
    DISCOVERY("discovery", "Discovery", "Discovery of different resources"),
    ENTITIES("entities", "Entities", "Entities and locations management"),
    PROTOCOLS("protocols", "Protocols", "Protocols management and operations"),
    SCHEDULER("scheduler", "Scheduler", "Scheduled jobs and tasks"),
    SECRETS("secrets", "Secrets", "Secrets management and operations"),
    SIGNING("signing", "Signing", "Signing operations and workflows"),
    WORKFLOWS("workflows", "Workflows", "Workflows management");

    private static final Module[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    Module(String code, String label) {
        this(code, label,null);
    }

    Module(String code, String label, String description) {
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
    public static Module findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(m -> m.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown module code {}", code)));
    }
}
