package com.otilm.api.model.client.signing.profile;

import com.otilm.api.model.client.signing.profile.scheme.SigningScheme;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.client.signing.profile.workflow.SigningWorkflowType;
import com.otilm.api.model.core.signing.SigningProtocol;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "SigningProfileListDto", description = "Signing Profile details for listing")
@ToString(callSuper = true)
public class SigningProfileListDto extends NameAndUuidDto {

    @Schema(description = "Version of the Signing Profile", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private int version;

    @Schema(description = "Description of the Signing Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Signing scheme (managed or delegated)", requiredMode = Schema.RequiredMode.REQUIRED)
    private SigningScheme signingScheme;

    @Schema(description = "Signing workflow type", requiredMode = Schema.RequiredMode.REQUIRED)
    private SigningWorkflowType signingWorkflowType;

    @Schema(description = "Whether the Signing Profile is enabled", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean enabled;

    @Schema(description = "List of enabled protocols on this Signing Profile.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<SigningProtocol> enabledProtocols = new ArrayList<>();
}
