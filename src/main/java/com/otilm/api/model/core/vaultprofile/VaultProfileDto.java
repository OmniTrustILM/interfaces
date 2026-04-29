package com.otilm.api.model.core.vaultprofile;

import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VaultProfileDto extends NameAndUuidDto {

    @Schema(description = "Description of the Vault profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Vault instance associated with this Vault profile", requiredMode = Schema.RequiredMode.REQUIRED)
    private NameAndUuidDto vaultInstance;

    @Schema(description = "Indicates whether the Vault profile is enabled", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean enabled;
}
