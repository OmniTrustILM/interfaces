package com.otilm.api.model.core.workflows;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateRuleRequestDto {

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
