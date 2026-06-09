package com.otilm.api.model.client.connector.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
public class HealthInfoComponent {

    @Schema(description = "Current status of component", requiredMode = Schema.RequiredMode.REQUIRED)
    private HealthStatus status;

    @Schema(description = "Additional details about the component status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<String, Object> details;

}
