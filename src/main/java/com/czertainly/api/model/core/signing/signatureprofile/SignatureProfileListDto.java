package com.czertainly.api.model.core.signing.signatureprofile;

import com.czertainly.api.model.common.NameAndUuidDto;
import com.czertainly.api.model.core.signing.signatureprofile.workflow.SigningWorkflowType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "SignatureProfileListDto", description = "Signature Profile details for listing")
@ToString(callSuper = true)
public class SignatureProfileListDto extends NameAndUuidDto {

    @Schema(description = "Version of the Signature Profile", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private int version;

    @Schema(description = "Description of the Signature Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Signing workflow type", requiredMode = Schema.RequiredMode.REQUIRED)
    private SigningWorkflowType signingWorkflowType;

    @NotNull
    @Schema(description = "Whether the Signature Profile is enabled", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean enabled;
}
