package com.otilm.api.model.core.workflows;

import com.otilm.api.model.common.validation.NullableNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateActionRequestDto {

    @Schema(
            description = "Name of the action",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @NullableNotBlank
    private String name;

    @Schema(
            description = "Description of the action"
    )
    private String description;

    @Schema(
            description = "List of UUIDs of existing executions to add to the action",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<String> executionsUuids = new ArrayList<>();

}
