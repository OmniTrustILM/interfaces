package com.czertainly.api.model.core.workflows;

import com.czertainly.api.model.core.search.FilterFieldSource;
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
            description = "Static data of the execution item. Must be null when sourceFieldSource is set"
    )
    private Serializable data;

    @Schema(
            description = "Source field source for mapping (META, DATA, or CUSTOM). When set, value is read from this source instead of static data.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private FilterFieldSource sourceFieldSource;

    @Schema(
            description = "Source field identifier for mapping (format: name|ContentType). Required when sourceFieldSource is set.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String sourceFieldIdentifier;

    @AssertTrue(message = "Field source and field identifier are required (set field execution) or notification profile UUID (send notification execution). " +
            "When sourceFieldSource is set, sourceFieldIdentifier is also required, fieldSource must be CUSTOM, sourceFieldSource must be META/DATA/CUSTOM, and data must be null.")
    private boolean isExecutionItemValid() {
        boolean baseValid = (fieldSource != null && fieldIdentifier != null) || notificationProfileUuid != null;
        if (!baseValid) return false;

        boolean hasSourceRef = sourceFieldSource != null || sourceFieldIdentifier != null;
        if (!hasSourceRef) return true;

        if (sourceFieldSource == null || sourceFieldIdentifier == null) return false;
        if (fieldSource != FilterFieldSource.CUSTOM) return false;
        if (sourceFieldSource != FilterFieldSource.META
                && sourceFieldSource != FilterFieldSource.DATA
                && sourceFieldSource != FilterFieldSource.CUSTOM) return false;
        return data == null;
    }

}
