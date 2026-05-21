package com.czertainly.api.model.core.workflows;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateExecutionRequestDto {

    @Schema(
            description = "Name of the execution",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Schema(
            description = "Description of the execution"
    )
    private String description;

    @Schema(
            description = "List of the execution items to add to execution",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<ExecutionItemRequestDto> items = new ArrayList<>();

}
