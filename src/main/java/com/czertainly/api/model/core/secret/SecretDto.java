package com.czertainly.api.model.core.secret;

import com.czertainly.api.model.common.NameAndUuidDto;
import com.czertainly.api.model.connector.secrets.SecretType;
import com.czertainly.api.model.core.compliance.ComplianceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SecretDto extends NameAndUuidDto {

    @Schema(description = "Type of the secret", requiredMode = Schema.RequiredMode.REQUIRED)
    private SecretType type;

    @Schema(description = "Description of the secret", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Name and UUID of the vault profile where the secret is stored", requiredMode = Schema.RequiredMode.REQUIRED)
    private NameAndUuidDto sourceVaultProfile;

    @Schema(description = "State of the secret", requiredMode = Schema.RequiredMode.REQUIRED)
    private SecretState state;

    @Schema(description = "Version of the secret", requiredMode = Schema.RequiredMode.REQUIRED)
    private int version;

    @Schema(description = "Indicates if the secret is currently enabled", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean enabled;

    @Schema(description = "Owner of the secret", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private NameAndUuidDto owner;

    @Schema(description = "Groups that have access to the secret", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<NameAndUuidDto> groups;

    @Schema(
            description = "Secret compliance status",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ComplianceStatus complianceStatus;

}
