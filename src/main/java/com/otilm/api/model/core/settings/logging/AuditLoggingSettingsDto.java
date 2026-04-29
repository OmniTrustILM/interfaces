package com.otilm.api.model.core.settings.logging;

import com.otilm.api.model.core.logging.enums.AuditLogOutput;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuditLoggingSettingsDto extends ResourceLoggingSettingsDto {

    @NotNull
    @Schema(
            description = "Type of audit logs output/storage channel",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private AuditLogOutput output = AuditLogOutput.NONE;

    @Schema(
            description = "Additional data should be logged"
    )
    private boolean verbose = false;

    @Hidden
    public void setResourceLogging(ResourceLoggingSettingsDto resourceLoggingSettingsDto) {
        this.setLogAllModules(resourceLoggingSettingsDto.isLogAllModules());
        this.setLogAllResources(resourceLoggingSettingsDto.isLogAllResources());
        this.setLoggedModules(resourceLoggingSettingsDto.getLoggedModules());
        this.setIgnoredModules(resourceLoggingSettingsDto.getIgnoredModules());
        this.setLoggedResources(resourceLoggingSettingsDto.getLoggedResources());
        this.setIgnoredResources(resourceLoggingSettingsDto.getIgnoredResources());
    }
}
