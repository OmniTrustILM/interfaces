package com.otilm.api.model.core.vaultprofile;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.core.auth.ObjectActionsAware;
import com.otilm.core.model.auth.ResourceAction;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VaultProfileDetailDto extends VaultProfileDto implements ObjectActionsAware {

    @Schema(description = "List of attributes of the Vault profile", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ResponseAttribute> attributes;

    @Schema(description = "List of custom attributes of the Vault profile",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> customAttributes;

    @Schema(description = ObjectActionsAware.ALLOWED_ACTIONS_DESCRIPTION)
    private List<ResourceAction> allowedActions;

}
