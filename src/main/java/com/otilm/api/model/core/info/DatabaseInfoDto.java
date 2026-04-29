package com.otilm.api.model.core.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DatabaseInfoDto {

    @Schema(description = "Database system product name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String system;

    @Schema(description = "Database product version", requiredMode = Schema.RequiredMode.REQUIRED)
    private String version;

}
