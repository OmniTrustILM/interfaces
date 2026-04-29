package com.otilm.api.model.core.workflows;

import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExecutionRequestDto {

    @Schema(
            description = "Name of the execution",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Schema(
            description = "Description of the execution"
    )
    private String description;

    @Schema(
            description = "Type of the execution",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ExecutionType type;

    @Schema(
            description = "Resource associated with the execution",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Resource resource;

    @Schema(
            description = "List of the execution items to add to execution",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<ExecutionItemRequestDto> items = new ArrayList<>();

}
