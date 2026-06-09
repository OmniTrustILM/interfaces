package com.otilm.api.model.core.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppInfoDto {

    @Schema(description = "Platform Application Name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Platform Core Version", requiredMode = Schema.RequiredMode.REQUIRED)
    private String version;

}
