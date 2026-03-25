package com.czertainly.api.model.core.signing.signatureprofile;

import com.czertainly.api.model.client.attribute.ResponseAttribute;
import com.czertainly.api.model.common.NameAndUuidDto;
import com.czertainly.api.model.core.signing.SigningProtocol;
import com.czertainly.api.model.core.signing.signatureprofile.scheme.SigningSchemeDto;
import com.czertainly.api.model.core.signing.signatureprofile.workflow.WorkflowDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "SignatureProfileDto", description = "Signature Profile detail")
public class SignatureProfileDto extends NameAndUuidDto {

    @Schema(description = "Description of the Signature Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Version of the Signature Profile", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private int version;

    @Schema(description = "Whether the Signature Profile is enabled", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean enabled;

    @Schema(description = "List of enabled protocols on this Signature Profile. Protocols are managed through dedicated enable/disable endpoints.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<SigningProtocol> enabledProtocols = new ArrayList<>();

    @Schema(description = "Signing Scheme configuration", requiredMode = Schema.RequiredMode.REQUIRED)
    private SigningSchemeDto signingScheme;

    @Schema(description = "Workflow-type-specific configuration. ", requiredMode = Schema.RequiredMode.REQUIRED)
    private WorkflowDto workflow;

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> customAttributes = new ArrayList<>();
}
