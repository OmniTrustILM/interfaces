package com.otilm.api.model.client.connector.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
public class HealthInfo {

    @Schema(description = "Current connector operational status",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private HealthStatus status;

    @Schema(description = "Health status of connector components",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            additionalPropertiesSchema = HealthInfoComponent.class
    )
    private Map<String, HealthInfoComponent> components;

}
