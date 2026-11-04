package com.otilm.api.model.client.signing.protocols.tsp;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.client.signing.profile.SimplifiedSigningProfileDto;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.auth.ObjectActionsAware;
import com.otilm.api.model.core.signing.TspAuthenticationMethod;
import com.otilm.api.model.core.vaultprofile.VaultProfileDto;
import com.otilm.core.model.auth.ResourceAction;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "TspProfileDto", description = "TSP (Timestamping Protocol) profile details")
@ToString(callSuper = true)
public class TspProfileDto extends NameAndUuidDto implements ObjectActionsAware {

    @Schema(description = "Description of the TSP Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = "TSP profile for production timestamping")
    private String description;

    @Schema(description = "Enabled flag of the TSP Profile", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "true")
    private boolean enabled;

    @Schema(description = "Default Signing Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private SimplifiedSigningProfileDto defaultSigningProfile;

    @Schema(description = "TSP URL for signing", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = "https://ilm.otilm.com/api/v1/protocols/tsp/tsp-profile-1")
    private String signingUrl;

    @Schema(description = "Vault profile that stores this profile's Basic credentials; required when Basic credentials are configured",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private VaultProfileDto vaultProfile;

    // Once the consumers are migrated, change mode to REQUIRED. Refer to TspProfileRequestDto.
    @Schema(description = "Authentication methods this TSP Profile accepts",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<TspAuthenticationMethod> allowedAuthenticationMethods = new ArrayList<>();

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> customAttributes = new ArrayList<>();

    @Schema(description = ObjectActionsAware.ALLOWED_ACTIONS_DESCRIPTION)
    private List<ResourceAction> allowedActions;

}
