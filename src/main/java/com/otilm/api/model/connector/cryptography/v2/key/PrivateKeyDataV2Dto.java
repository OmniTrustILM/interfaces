package com.otilm.api.model.connector.cryptography.v2.key;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Non-exportable private-key descriptor without key material.
 */
@Schema(name = "PrivateKeyDataV2Dto", description = "Private-key descriptor without key material",
        additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public final class PrivateKeyDataV2Dto extends KeyDataV2Dto {

    public PrivateKeyDataV2Dto() {
        super(KeyRoleV2.PRIVATE);
    }

    @Override
    protected KeyRoleV2 expectedType() {
        return KeyRoleV2.PRIVATE;
    }

    @Override
    @Schema(description = "Role of the key", type = "string", implementation = String.class,
            allowableValues = "Private", _const = "Private",
            requiredMode = Schema.RequiredMode.REQUIRED)
    public KeyRoleV2 getType() {
        return super.getType();
    }
}
