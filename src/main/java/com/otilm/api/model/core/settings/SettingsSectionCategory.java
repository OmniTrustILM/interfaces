package com.otilm.api.model.core.settings;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

public enum SettingsSectionCategory implements IPlatformEnum {
    PLATFORM_UTILS("utils", "Utils", SettingsSection.PLATFORM, "Platform utils settings"),
    PLATFORM_CERTIFICATES("certificates", "Certificates", SettingsSection.PLATFORM, "Platform certificates settings"),
    AUDIT_LOGGING("audit", "Audit Logging", SettingsSection.LOGGING, "Audit logging settings"),
    EVENT_LOGGING("event", "Event Logging", SettingsSection.LOGGING, "Event logging settings"),
    OAUTH2_PROVIDER("oauth2Provider", "OAuth2 Provider", SettingsSection.AUTHENTICATION, "OAuth2 provider settings");

    private static final SettingsSectionCategory[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(
            description = "Setting section category",
            examples = {"platform"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final String code;

    @Schema(
            description = "Name",
            examples = {"Utils"}
    )
    private final String label;

    @Schema(
            description = "Description",
            examples = {"Platform utils settings"}
    )
    private final String description;

    @Schema(
            description = "Settings section to which category belongs",
            examples = {"Platform utils settings"}
    )
    private final SettingsSection section;

    SettingsSectionCategory(String code, String label, SettingsSection section) {
        this(code, label, section, null);
    }

    SettingsSectionCategory(String code, String label, SettingsSection section, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.section = section;
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
    public static SettingsSectionCategory findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown settings section {}", code)));
    }
}
