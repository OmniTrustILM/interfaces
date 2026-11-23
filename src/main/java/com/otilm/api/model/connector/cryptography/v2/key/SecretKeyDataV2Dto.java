package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Non-exportable secret-key descriptor without key material.
 */
@Schema(name = "SecretKeyDataV2Dto", description = "Secret-key descriptor without key material",
        additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
@JsonPropertyOrder({"type", "algorithm", "length", "metadata"})
@ToString(callSuper = true)
public final class SecretKeyDataV2Dto extends KeyDataV2Dto {

    @Getter
    @Setter
    @Schema(description = "Key length in bits", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
    @NotNull(message = "key length is required")
    @Min(value = 1, message = "key length must be positive")
    private Integer length;

    public SecretKeyDataV2Dto() {
        super(KeyTypeV2.SECRET);
    }

    @Override
    @Schema(description = "Role of the key", type = "string", implementation = String.class, allowableValues = "Secret",
            _const = "Secret", requiredMode = Schema.RequiredMode.REQUIRED)
    public KeyTypeV2 getType() {
        return super.getType();
    }
}
