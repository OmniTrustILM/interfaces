package com.otilm.api.model.core.workflows;

import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RuleRequestDto {

    @Schema(
            description = "Name of the rule",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Schema(
            description = "Description of the rule"
    )
    private String description;

    @Schema(
            description = "Resource associated with the rule",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Resource resource;

    @Schema(
            description = "List of UUIDs of existing conditions to add to the rule",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<String> conditionsUuids = new ArrayList<>();

}
