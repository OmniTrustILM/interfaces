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
public class KeyRequestDto {

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
            description = "UUIDs of the groups to associate with key"
    )
    private List<String> groupUuids;

    @Schema(
            description = "List of Attributes to create a Key",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute> attributes;

    @Schema(
            description = "Custom Attributes for the key"
    )
    private List<RequestAttribute> customAttributes;

    @Schema(description = "Enabled status of created key. True = Enabled, False = Disabled",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            defaultValue = "false")
    private Boolean enabled;

}
