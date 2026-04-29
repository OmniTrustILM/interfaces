package com.otilm.api.model.core.workflows;

import com.otilm.api.model.core.search.FilterFieldSource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class ExecutionItemDto {
    @Schema(
            description = "Source of the field in the execution item (required in case of set field execution type)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private FilterFieldSource fieldSource;

    @Schema(
            description = "Field identifier of the execution item (required in case of set field execution type)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String fieldIdentifier;

    @Schema(
            description = "UUID of the Notification profile (required in case of send notification execution type)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String notificationProfileUuid;

    @Schema(
            description = "Name of the Notification profile (required in case of send notification execution type)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String notificationProfileName;

    @Schema(
            description = "Data of the execution item"
    )
    private Serializable data;

}
