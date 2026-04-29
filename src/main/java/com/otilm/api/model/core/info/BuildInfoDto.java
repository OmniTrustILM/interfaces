package com.otilm.api.model.core.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class BuildInfoDto {

    @Schema(description = "App build timestamp", requiredMode = Schema.RequiredMode.REQUIRED, example = "2024-06-01T12:00:00Z")
    private OffsetDateTime timestamp;

    @Schema(description = "Git branch name", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "main")
    private String branch;

    @Schema(description = "Abbreviated ID of the commit", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "a1b2c3d")
    private String commitId;

}
