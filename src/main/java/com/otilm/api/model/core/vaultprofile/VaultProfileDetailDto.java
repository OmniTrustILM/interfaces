package com.otilm.api.model.core.vaultprofile;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class VaultProfileDetailDto extends VaultProfileDto {

    @Schema
            (
                    description = "List of attributes of the Vault profile",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
    private List<ResponseAttribute> attributes;

    @Schema(
            description = "List of custom attributes of the Vault profile",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> customAttributes;
}
