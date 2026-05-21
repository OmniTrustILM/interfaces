package com.czertainly.api.model.core.workflows;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class UpdateConditionRequestDto {

    @Schema(
            description = "Name of the condition",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Name cannot be blank")
    private String name;


    @Schema(
            description = "Description of the condition"
    )
    private String description;

    @Schema(
            description = "List of the condition items to add to condition",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<ConditionItemRequestDto> items;

}
