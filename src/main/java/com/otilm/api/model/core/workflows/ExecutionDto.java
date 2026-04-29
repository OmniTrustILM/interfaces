package com.otilm.api.model.core.workflows;

import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExecutionDto extends NameAndUuidDto {

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
            description = "List of the execution items",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<ExecutionItemDto> items = new ArrayList<>();

}
