package com.czertainly.api.model.core.vaultprofile;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class VaultProfileRequestDto {

    @Schema(description = "Name of the Vault profile", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String name;

    @Schema(description = "Description of the Vault profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "List of attributes of the Vault profile", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> attributes = new ArrayList<>();

    @Schema(description = "List of custom attributes of the Vault profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> customAttributes = new ArrayList<>();
}
