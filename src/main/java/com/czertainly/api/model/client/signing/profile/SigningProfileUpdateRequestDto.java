package com.czertainly.api.model.client.signing.profile;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.client.signing.profile.scheme.SigningSchemeRequestDto;
import com.czertainly.api.model.client.signing.profile.workflow.WorkflowRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(name = "SigningProfileUpdateRequestDto",
        description = """
Request to update an existing Signing Profile.
If there are existing Digital Signatures produced using this Signing Profile, creates a new version of Signing Profile.
Otherwise updates the latest version in-place.
""")
public class SigningProfileUpdateRequestDto {

    @NotBlank
    @Schema(description = "Name of the Signing Profile", requiredMode = Schema.RequiredMode.REQUIRED, example = "SigningProfile-1")
    private String name;

    @Schema(description = "Description of the Signing Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "Profile for document signing")
    private String description;

    @NotNull
    @Schema(description = "Whether the Signing Profile is enabled", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
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
