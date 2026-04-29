package com.otilm.api.model.core.vault;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class VaultInstanceUpdateRequestDto {

    @Schema(description = "Description of the Vault instance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "List of attributes of the Vault instance", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> attributes = new ArrayList<>();

    @Schema(description = "List of custom attributes of the Vault instance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> customAttributes = new ArrayList<>();
}
