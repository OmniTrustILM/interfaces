package com.otilm.api.model.connector.cryptography.v2.key;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Non-exportable secret-key descriptor without key material.
 */
@Schema(name = "SecretKeyDataV2Dto", description = "Secret-key descriptor without key material",
        additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public final class SecretKeyDataV2Dto extends KeyDataV2Dto {

    public SecretKeyDataV2Dto() {
        super(KeyRoleV2.SECRET);
    }

    @Override
    protected KeyRoleV2 expectedType() {
        return KeyRoleV2.SECRET;
    }

    @Override
    @Schema(description = "Role of the key", type = "string", implementation = String.class,
            allowableValues = "Secret", _const = "Secret",
            requiredMode = Schema.RequiredMode.REQUIRED)
    public KeyRoleV2 getType() {
        return super.getType();
    }
}
