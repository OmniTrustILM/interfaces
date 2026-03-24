package com.czertainly.api.model.core.signing.workflow;

import com.czertainly.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "SigningWorkflowConfigurationListDto", description = "Signing Workflow Configuration details for listing")
@ToString
public class SigningWorkflowConfigurationListDto extends NameAndUuidDto {

    @Schema(description = "Signing workflow type", requiredMode = Schema.RequiredMode.REQUIRED, examples = {SigningWorkflowType.Codes.TIMESTAMPING})
    private SigningWorkflowType type;
}
