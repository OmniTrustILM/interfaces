package com.otilm.api.model.core.workflows;

import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConditionRequestDto {

    @Schema(
            description = "Name of the condition",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Schema(
            description = "Description of the condition"
    )
    private String description;

    @Schema(
            description = "Type of the condition",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ConditionType type;

    @Schema(
            description = "Resource associated with the condition",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Resource resource;

    @Schema(
            description = "List of the condition items to add to condition",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<ConditionItemRequestDto> items = new ArrayList<>();

}
