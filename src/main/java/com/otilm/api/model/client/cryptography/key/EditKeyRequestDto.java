package com.otilm.api.model.client.cryptography.key;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class EditKeyRequestDto {

    @Schema(
            description = "UUID of the token profile",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String tokenProfileUuid;

    @Schema(
            description = "Name of the Cryptographic Key",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @Schema(
            description = "Description of the Cryptographic Key",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String description;

    @Schema(
            description = "Key Owner UUID"
    )
    private String ownerUuid;

    @Schema(
            description = "UUIDs of the groups to associate with key"
    )
    private List<String> groupUuids;

    @Schema(
            description = "List of Custom Attributes"
    )
    private List<RequestAttribute> customAttributes;
}
