package com.otilm.api.model.core.secret;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.client.metadata.MetadataResponseDto;
import com.otilm.api.model.core.auth.ObjectActionsAware;
import com.otilm.core.model.auth.ResourceAction;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SecretDetailDto extends SecretDto implements ObjectActionsAware {

    @Schema(description = "List of vault profiles where the secret is stored",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<SyncVaultProfileDto> syncVaultProfiles;

    @Schema(description = "Date and time when the secret was created", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime createdAt;

    @Schema(description = "Date and time when the secret was last updated", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime updatedAt;

    @Schema(description = "List of attributes associated with the secret", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ResponseAttribute> attributes;

    @Schema(description = "List of custom attributes associated with the secret",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> customAttributes;

    @Schema(description = "List of metadata associated with the secret",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataResponseDto> metadata;

    @Schema(description = ObjectActionsAware.ALLOWED_ACTIONS_DESCRIPTION)
    private List<ResourceAction> allowedActions;

}
