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

    // Common interfaces
    INFO("info", "Info", InterfaceCategory.COMMON),
    HEALTH("health", "Health", InterfaceCategory.COMMON),
    METRICS("metrics", "Metrics", InterfaceCategory.COMMON),
    ATTRIBUTES("attributes", "Attributes", InterfaceCategory.COMMON),
    // Functional interfaces
    AUTHORITY("authority", "Authority", InterfaceCategory.FUNCTIONAL),
    DISCOVERY("discovery", "Discovery", InterfaceCategory.FUNCTIONAL),
    ENTITY("entity", "Entity", InterfaceCategory.FUNCTIONAL),
    COMPLIANCE("compliance", "Compliance", InterfaceCategory.FUNCTIONAL),
    CRYPTOGRAPHY("cryptography", "Cryptography", InterfaceCategory.FUNCTIONAL),
    NOTIFICATION("notification", "Notification", InterfaceCategory.FUNCTIONAL),
    SECRET("secret", "Secret", InterfaceCategory.FUNCTIONAL),
    SIGNATURE_FORMATTING("signatureFormatting", "Signature Formatting", InterfaceCategory.FUNCTIONAL),
    SIGNING("signing", "Signing", InterfaceCategory.FUNCTIONAL);

    /**
     * Groups a connector interface as a common baseline interface (info/health/metrics/attributes) or a
     * functional/operational provider (the capabilities a connector supplies). This is a grouping only —
     * which specific interfaces are required for a connector to register is enforced by Core, independently
     * of this category.
     */
    public enum InterfaceCategory {
        COMMON,
        FUNCTIONAL
    }

    private static final ConnectorInterface[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Connector interface code",
            examples = {"authority"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final InterfaceCategory category;
    private final String description;

    ConnectorInterface(String code, String label, InterfaceCategory category) {
        this(code, label, category, null);
    }

    ConnectorInterface(String code, String label, InterfaceCategory category, String description) {
        this.code = code;
        this.label = label;
        this.category = category;
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

    public InterfaceCategory getCategory() {
        return this.category;
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
