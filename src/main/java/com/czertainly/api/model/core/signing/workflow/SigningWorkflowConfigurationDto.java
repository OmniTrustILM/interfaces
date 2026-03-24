package com.czertainly.api.model.core.signing.workflow;

import com.czertainly.api.model.client.attribute.ResponseAttribute;
import com.czertainly.api.model.common.NameAndUuidDto;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TimestampingConfigurationDto.class, name = SigningWorkflowType.Codes.TIMESTAMPING),
})
@Schema(implementation = SigningWorkflowConfigurationInterface.class)
public abstract class SigningWorkflowConfigurationDto extends NameAndUuidDto implements SigningWorkflowConfigurationInterface {

    @NotNull
    @Schema(description = "Signing workflow type", requiredMode = Schema.RequiredMode.REQUIRED, examples = {SigningWorkflowType.Codes.TIMESTAMPING})
    private final SigningWorkflowType type;

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> customAttributes = new ArrayList<>();

    protected SigningWorkflowConfigurationDto(SigningWorkflowType type) {
        this.type = type;
    }
}
