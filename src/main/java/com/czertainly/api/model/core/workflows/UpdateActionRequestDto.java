package com.czertainly.api.model.core.workflows;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateActionRequestDto {

    @Schema(
            description = "Name of the action",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Schema(
            description = "Description of the action"
    )
    private String description;

    @Schema(
            description = "List of UUIDs of existing executions to add to the action",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<String> executionsUuids = new ArrayList<>();

}
