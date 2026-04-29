package com.otilm.api.model.core.workflows;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateExecutionRequestDto {

    @Schema(
            description = "Description of the execution"
    )
    private String description;

    @Schema(
            description = "List of the execution items to add to execution",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<ExecutionItemRequestDto> items = new ArrayList<>();

}
