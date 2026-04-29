package com.otilm.api.model.core.settings.logging;

import com.otilm.api.model.core.settings.SettingsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class LoggingSettingsDto implements SettingsDto {

    @Valid
    @NotNull
    @Schema(description = "Settings of audit logging", requiredMode = Schema.RequiredMode.REQUIRED)
    private AuditLoggingSettingsDto auditLogs;

    @Valid
    @NotNull
    @Schema(description = "Settings of event logging", requiredMode = Schema.RequiredMode.REQUIRED)
    private ResourceLoggingSettingsDto eventLogs;

}
