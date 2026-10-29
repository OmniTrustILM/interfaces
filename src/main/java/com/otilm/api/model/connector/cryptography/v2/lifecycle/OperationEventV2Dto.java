package com.otilm.api.model.connector.cryptography.v2.lifecycle;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "OperationEventV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE,
        description = "One step of a signature activation as the connector observed it, kept by the platform "
                + "as audit history.")
public class OperationEventV2Dto {

    @Schema(description = "Step the event reports", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "type is required")
    private OperationEventType type;

    @Schema(description = "When the step happened, as the connector observed it",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "at is required")
    private OffsetDateTime at;

    @Schema(description = "Opaque technology-specific detail of the step, such as the module's own identifiers")
    private Map<String, Object> vendorDetail;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported v2 operation event property: " + property);
    }
}
