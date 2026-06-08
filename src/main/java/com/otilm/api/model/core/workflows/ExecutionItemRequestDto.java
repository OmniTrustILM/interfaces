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
            description = "Static data of the execution item. Must be null when sourceFieldSource or sourceFieldIdentifier is set"
    )
    private Serializable data;

    @Schema(
            description = "Source field source for mapping (META, DATA, or CUSTOM). When set, value is read from this source instead of static data. Must be provided together with sourceFieldIdentifier; neither field is valid on its own.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private FilterFieldSource sourceFieldSource;

    @Schema(
            description = "Source field identifier for mapping (format: name|ContentType). Must be provided together with sourceFieldSource; neither field is valid on its own.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String sourceFieldIdentifier;

    @AssertTrue(message = "Field source and field identifier are required (set field execution) or notification profile UUID (send notification execution).")
    private boolean isExecutionItemValid() {
        return (fieldSource != null && fieldIdentifier != null) || notificationProfileUuid != null;
    }

    @AssertTrue(message = "sourceFieldSource and sourceFieldIdentifier must both be set or both be absent")
    private boolean sourceFieldsMatch() {
        return (sourceFieldSource == null) == (sourceFieldIdentifier == null);
    }

    @AssertTrue(message = "When sourceFieldSource and sourceFieldIdentifier are set, fieldSource must be CUSTOM and fieldIdentifier must be non-null")
    private boolean isTargetCustomWhenSourceRefSet() {
        return sourceFieldsNotDefined() || (fieldSource == FilterFieldSource.CUSTOM && fieldIdentifier != null);
    }

    @AssertTrue(message = "sourceFieldSource cannot be PROPERTY")
    private boolean sourceFieldSourceValid() {
        return sourceFieldSource != FilterFieldSource.PROPERTY;
    }

    @AssertTrue(message = "data must be null when sourceFieldSource and sourceFieldIdentifier are defined")
    private boolean sourceFieldsNullData() {
        return sourceFieldsNotDefined() || data == null;
    }

    private boolean sourceFieldsNotDefined() {
        return sourceFieldSource == null || sourceFieldIdentifier == null;
    }
}
