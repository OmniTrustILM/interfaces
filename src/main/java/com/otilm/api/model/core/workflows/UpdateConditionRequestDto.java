package com.otilm.api.model.core.workflows;

import com.otilm.api.model.common.validation.NullableNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class UpdateConditionRequestDto {

    @Schema(
            description = "Name of the condition",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @NullableNotBlank
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
