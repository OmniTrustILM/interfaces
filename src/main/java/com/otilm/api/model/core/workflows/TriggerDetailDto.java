package com.otilm.api.model.core.workflows;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
public class TriggerDetailDto extends TriggerDto {

    @Schema(
            description = "List of Rules in the Rule Trigger",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RuleDetailDto> rules;

    @Schema(
            description = "List of Action Groups in the Rule Trigger",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<ActionDetailDto> actions;
}
