package com.otilm.api.model.core.workflows;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class RuleDetailDto extends RuleDto {

    @Schema(
            description = "List of conditions in the Rule",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<ConditionDto> conditions = new ArrayList<>();
}
