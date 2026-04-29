package com.otilm.api.model.core.workflows;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ActionDetailDto extends ActionDto {

    @Schema(
            description = "List of executions",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<ExecutionDto> executions = new ArrayList<>();

}
