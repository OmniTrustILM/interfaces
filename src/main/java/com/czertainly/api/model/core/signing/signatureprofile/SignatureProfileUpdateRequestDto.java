package com.czertainly.api.model.core.signing.signatureprofile;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.core.signing.signatureprofile.scheme.SigningSchemeRequestDto;
import com.czertainly.api.model.core.signing.signatureprofile.workflow.WorkflowRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(name = "SignatureProfileUpdateRequestDto", description = "Request to update an existing Signature Profile")
public class SignatureProfileUpdateRequestDto {

    @NotBlank
    @Schema(description = "Name of the Signature Profile", requiredMode = Schema.RequiredMode.REQUIRED, example = "SignatureProfile-1")
    private String name;

    @Schema(description = "Description of the Signature Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "Profile for document signing")
    private String description;

    @NotNull
    @Schema(description = "Whether the Signature Profile is enabled", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean enabled;

    @NotNull
    @Valid
    @Schema(description = "Signing scheme configuration (who holds the key and how the cryptographic operation is performed)",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private SigningSchemeRequestDto signingScheme;

    @NotNull
    @Valid
    @Schema(description = "Workflow-type-specific configuration.", requiredMode = Schema.RequiredMode.REQUIRED)
    private WorkflowRequestDto workflowConfiguration;

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> customAttributes = new ArrayList<>();
}
