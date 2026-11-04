package com.otilm.api.model.client.signing.profile;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.client.signing.profile.record.SigningRecordPolicyDto;
import com.otilm.api.model.client.signing.profile.scheme.SigningSchemeDto;
import com.otilm.api.model.client.signing.profile.workflow.WorkflowDto;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.auth.ObjectActionsAware;
import com.otilm.api.model.core.signing.SigningProtocol;
import com.otilm.core.model.auth.ResourceAction;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "SigningProfileDto", description = "Signing Profile detail")
@ToString(callSuper = true)
public class SigningProfileDto extends NameAndUuidDto implements ObjectActionsAware {

    @Schema(description = "Description of the Signing Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Version of the Signing Profile", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private int version;

    @Schema(description = "Whether the Signing Profile is enabled", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean enabled;

    @Schema(description = "List of enabled protocols on this Signing Profile. Protocols are managed through dedicated enable/disable endpoints.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<SigningProtocol> enabledProtocols = new ArrayList<>();

    @Schema(description = "Signing Scheme configuration", requiredMode = Schema.RequiredMode.REQUIRED)
    private SigningSchemeDto signingScheme;

    @Schema(description = "Workflow-type-specific configuration. ", requiredMode = Schema.RequiredMode.REQUIRED)
    private WorkflowDto workflow;

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> customAttributes = new ArrayList<>();

    @Schema(description = "Effective Signing Record policy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private SigningRecordPolicyDto recordPolicy;

    @Schema(description = ObjectActionsAware.ALLOWED_ACTIONS_DESCRIPTION)
    private List<ResourceAction> allowedActions;

}
