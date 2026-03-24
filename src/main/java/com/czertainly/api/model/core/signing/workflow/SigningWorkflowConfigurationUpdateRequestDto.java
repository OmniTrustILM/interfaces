package com.czertainly.api.model.core.signing.workflow;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TimestampingConfigurationUpdateRequestDto.class, name = SigningWorkflowType.Codes.TIMESTAMPING),
})
@Schema(implementation = SigningWorkflowConfigurationUpdateRequestInterface.class)
public abstract class SigningWorkflowConfigurationUpdateRequestDto implements SigningWorkflowConfigurationUpdateRequestInterface {
    @NotBlank
    @Schema(description = "Name of the Signing Workflow Configuration", requiredMode = Schema.RequiredMode.REQUIRED, example = "Workflow-1")
    private String name;

    @NotNull
    @Schema(description = "Signing workflow type", requiredMode = Schema.RequiredMode.REQUIRED, examples = {SigningWorkflowType.Codes.TIMESTAMPING})
    private final SigningWorkflowType type;

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> customAttributes = new ArrayList<>();

    protected SigningWorkflowConfigurationUpdateRequestDto(SigningWorkflowType type) {
        this.type = type;
    }
}
