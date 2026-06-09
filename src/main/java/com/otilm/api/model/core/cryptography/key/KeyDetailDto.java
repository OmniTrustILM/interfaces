package com.otilm.api.model.core.cryptography.key;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.certificate.group.GroupDto;
import com.otilm.api.model.core.compliance.ComplianceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class KeyDetailDto extends NameAndUuidDto {

    @Schema(description = "Description of the Key",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String description;

    @Schema(description = "Creation time of the Key. If the key is discovered from the connector, then it will be returned",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private OffsetDateTime creationTime;

    @Schema(
            description = "UUID of the Token Profile",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String tokenProfileUuid;

    @Schema(
            description = "Name of the Token Profile",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String tokenProfileName;

    @Schema(
            description = "Token Instance UUID",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String tokenInstanceUuid;

    @Schema(
            description = "Token Instance Name",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String tokenInstanceName;

    @Schema(
            description = "Custom Attributes for the Cryptographic Key",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<ResponseAttribute> customAttributes;

    @Schema(
            description = "Attributes for the Cryptographic Key",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<ResponseAttribute> attributes;

    @Schema(
            description = "Owner of the Key",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String owner;

    @Schema(
            description = "UUID of the owner of the Key",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String ownerUuid;

    @Schema(
            description = "Groups associated to the key",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<GroupDto> groups;

    @Schema(
            description = "Key Objects",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<KeyItemDetailDto> items;

    @Schema(
            description = "List of associated items",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<KeyAssociationDto> associations;

    @Schema(
            description = "Key compliance status",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ComplianceStatus complianceStatus;

}
