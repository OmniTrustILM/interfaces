package com.otilm.api.model.core.secret;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
public class SecretUpdateObjectsDto {

    @Schema(description = "UUID of the vault profile where the secret is stored")
    private UUID sourceVaultProfileUuid;

    @Schema(description = "Secret attributes, to be provided if new source vault profile is assigned to a different vault then the original source vault profile")
    private List<RequestAttribute> secretAttributes = new ArrayList<>();

    @Schema(description = "Secret Groups UUIDs (set to empty array [] to remove secrets from all groups)")
    private Set<UUID> groupUuids;

    @Schema(description = "Secret owner user UUID (set to empty string to remove owner of the secret)")
    private String ownerUuid;
}
