package com.otilm.api.model.core.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppInfoDto {

    @Schema(description = "CZERTAINLY Application name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "CZERTAINLY Core version", requiredMode = Schema.RequiredMode.REQUIRED)
    private String version;

}
