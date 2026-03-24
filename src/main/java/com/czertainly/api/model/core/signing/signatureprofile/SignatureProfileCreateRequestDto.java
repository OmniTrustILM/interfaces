package com.czertainly.api.model.core.signing.signatureprofile;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Schema(name = "SignatureProfileCreateRequestDto", description = "Request to create a new Signature Profile")
public class SignatureProfileCreateRequestDto {

    @NotBlank
    @Schema(description = "Name of the Signature Profile", requiredMode = Schema.RequiredMode.REQUIRED, example = "SignatureProfile-1")
    private String name;

    @Schema(description = "Description of the Signature Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "Profile for document signing")
    private String description;

    @NotNull
    @Schema(description = "UUID of the Signing Workflow Configuration to use", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID signingWorkflowConfigurationUuid;

    @NotNull
    @Schema(description = "Whether the Signature Profile is enabled", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean enabled;

    @NotNull
    @Valid
    @Schema(description = "'Signing with' configuration", requiredMode = Schema.RequiredMode.REQUIRED)
    private SigningSchemeRequestDto signingWith;

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> customAttributes = new ArrayList<>();
}
