package com.otilm.api.model.core.workflows;

import com.otilm.api.model.core.search.FilterConditionOperator;
import com.otilm.api.model.core.search.FilterFieldSource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ConditionItemDto {
    @Schema(
            description = "Source of the field in the condition item",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private FilterFieldSource fieldSource;

    @Schema(
            description = "Field identifier of the condition item",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String fieldIdentifier;

    @Schema(
            description = "Operator of the condition item",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private FilterConditionOperator operator;

    @Schema(
            description = "Value of the condition item"
    )
    private Object value;
}
