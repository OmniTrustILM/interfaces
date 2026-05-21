package com.czertainly.api.model.core.workflows;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateRuleRequestDto {

    @Schema(description = "Name of the Rule", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Schema(
            description = "Description of the Rule"
    )
    private String description;

    @Schema(
            description = "List of UUIDs of existing conditions to add to the rule",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<String> conditionsUuids = new ArrayList<>();

}
