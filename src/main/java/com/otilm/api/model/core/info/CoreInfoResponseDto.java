package com.otilm.api.model.core.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CoreInfoResponseDto {

    @Schema(description = "ILM Application information", requiredMode = Schema.RequiredMode.REQUIRED)
    private AppInfoDto app;

    @Schema(description = "ILM Application build information. May be omitted depending on deployment configuration", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private BuildInfoDto build;

    @Schema(description = "Database information", requiredMode = Schema.RequiredMode.REQUIRED)
    private DatabaseInfoDto db;

}
