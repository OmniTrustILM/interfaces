package com.otilm.api.model.core.vault;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class VaultInstanceRequestDto {

    @Schema(description = "UUID of the Connector associated with this Vault instance", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private UUID connectorUuid;

    @Schema(description = "UUID of the Connector Interface associated with this Vault instance", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private UUID interfaceUuid;

    @Schema(description = "Name of the Vault instance", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String name;

    @Schema(description = "Description of the Vault instance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "List of attributes of the Vault instance", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> attributes = new ArrayList<>();

    @Schema(description = "List of custom attributes of the Vault instance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> customAttributes = new ArrayList<>();
}
