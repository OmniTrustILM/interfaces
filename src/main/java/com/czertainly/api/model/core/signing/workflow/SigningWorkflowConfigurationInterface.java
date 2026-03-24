package com.czertainly.api.model.core.signing.workflow;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(
        name = "SigningWorkflowConfigurationInterface",
        description = "Signing Workflow Configuration dependent on workflow type",
        type = "object",
        discriminatorProperty = "type",
        discriminatorMapping = {
                @DiscriminatorMapping(value = SigningWorkflowType.Codes.TIMESTAMPING, schema = TimestampingConfigurationDto.class),
        },
        oneOf = {
                TimestampingConfigurationDto.class,
        })
public interface SigningWorkflowConfigurationInterface extends Serializable {
    @Schema(description = "Signing Workflow type", requiredMode = Schema.RequiredMode.REQUIRED, examples = {SigningWorkflowType.Codes.TIMESTAMPING})
    SigningWorkflowType getType();
}
