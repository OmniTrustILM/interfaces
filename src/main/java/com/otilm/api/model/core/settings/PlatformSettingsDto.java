package com.otilm.api.model.core.settings;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PlatformSettingsDto implements SettingsDto {

    @Schema(description = "Utils settings of the platform", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UtilsSettingsDto utils;

    @Schema(description = "Settings applicable to all certificates in inventory by default", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CertificateSettingsDto certificates;

}
