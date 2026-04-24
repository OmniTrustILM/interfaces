package com.czertainly.api.model.core.workflows;

import com.czertainly.api.model.common.NameAndUuidDto;
import com.czertainly.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TriggeredEventOriginDto {

    @Schema(description = "Resource of origin that triggered the event.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Resource type;

    @Schema(description = "Origin object reference")
    private NameAndUuidDto resource;
}
