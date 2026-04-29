package com.otilm.api.model.core.enums;

import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EnumItemDto {

    @Schema(
            requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Enum item code",
            examples = {"X509"}
    )
    private String code;

    @Schema(
            requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Enum item display label",
            examples = {"X.509"}
    )
    private String label;

    @Schema(
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            description = "Enum item description",
            examples = {"X.509 Certificate type"}
    )
    private String description;
}
