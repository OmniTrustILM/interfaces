package com.czertainly.api.model.client.signing.profile;

import com.czertainly.api.model.common.NameAndUuidDto;
import com.czertainly.api.model.client.signing.profile.workflow.SigningWorkflowType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "SigningProfileListDto", description = "Signing Profile details for listing")
@ToString(callSuper = true)
public class SigningProfileListDto extends NameAndUuidDto {

    @Schema(description = "Version of the Signing Profile", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private int version;

    @Schema(description = "Description of the Signing Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Signing workflow type", requiredMode = Schema.RequiredMode.REQUIRED)
    private SigningWorkflowType signingWorkflowType;

    @Schema(description = "Whether the Signing Profile is enabled", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean enabled;
}
