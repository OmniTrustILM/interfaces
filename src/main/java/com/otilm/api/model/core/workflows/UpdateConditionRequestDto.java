package com.otilm.api.model.core.workflows;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class UpdateConditionRequestDto {

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
