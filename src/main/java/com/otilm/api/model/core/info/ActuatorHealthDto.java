package com.otilm.api.model.core.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ActuatorHealthDto {

    @Schema(
            description = "Status of the application health",
            requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"UNKNOWN", "UP", "DOWN", "OUT_OF_SERVICE"}
    )
    private String status;

}
