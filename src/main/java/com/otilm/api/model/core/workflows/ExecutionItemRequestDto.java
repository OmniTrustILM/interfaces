package com.otilm.api.model.core.workflows;

import com.otilm.api.model.core.search.FilterFieldSource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

import java.io.Serializable;

@Data
public class ExecutionItemRequestDto {
    @Schema(
            description = "Source of the field in the execution item",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private FilterFieldSource fieldSource;

    @Schema(
            description = "Field identifier of the execution item",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String fieldIdentifier;

    @Schema(
            description = "UUID of the Notification profile",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String notificationProfileUuid;

    @Schema(
            description = "Data of the execution item"
    )
    private Serializable data;

    @AssertTrue(message = "Field source and field identifier are required (set field execution) or notification profile UUID (send notification execution)")
    private boolean isExecutionItemValid() {
        return (fieldSource != null && fieldIdentifier != null) || notificationProfileUuid != null;
    }

}
