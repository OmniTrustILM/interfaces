package com.otilm.api.model.client.signing.profile;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.signing.profile.record.SigningRecordPolicyRequestDto;
import com.otilm.api.model.client.signing.profile.record.validation.ValidSigningRecordPolicy;
import com.otilm.api.model.client.signing.profile.scheme.SigningSchemeRequestDto;
import com.otilm.api.model.client.signing.profile.validation.ValidManagedSigningFormatterConnector;
import com.otilm.api.model.client.signing.profile.workflow.WorkflowRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import com.otilm.api.model.common.validation.ValidName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(name = "SigningProfileRequestDto", description = "Request to create or update a Signing Profile")
@ValidManagedSigningFormatterConnector
@ValidSigningRecordPolicy
public class SigningProfileRequestDto {
    @NotBlank
    @ValidName
    @Schema(description = "Name of the Signing Profile", requiredMode = Schema.RequiredMode.REQUIRED, example = "SigningProfile-1")
    private String name;

    @Schema(description = "Description of the Signing Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "Profile for content signing")
    private String description;

    @NotNull
    @Valid
    @Schema(description = "Signing scheme configuration (who holds the key and how the cryptographic operation is performed)",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private SigningSchemeRequestDto signingScheme;

    @NotNull
    @Valid
    @Schema(description = "Workflow-type-specific configuration.", requiredMode = Schema.RequiredMode.REQUIRED)
    private WorkflowRequestDto workflow;

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> customAttributes = new ArrayList<>();

    @Valid
    @Schema(description = "Signing Record policy. When omitted, defaults are used (no signing record).",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private SigningRecordPolicyRequestDto recordPolicy;
}
