package com.otilm.api.model.connector.cryptography.v2.operations;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.data.CipherDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.validation.UniqueIdentifiers;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Body for {@code POST /v2/cryptographyProvider/operations/encrypt} and {@code .../decrypt}.
 */
@Getter
@Setter
@ToString(callSuper = true)
@Schema(name = "CipherDataRequestV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class CipherDataRequestV2Dto extends KeyScopedRequestV2Dto {

    @Schema(description = "Batch-wide cipher settings. Must include exactly one v3 string attribute named "
            + "encryptionAlgorithm with UUID 5e364467-fa95-4253-907b-0c73cdfb2be7, selecting exactly one code "
            + "offered by the corresponding encryption or decryption attribute schema. "
            + "Algorithm codes are values from the EncryptionAlgorithm enum. RSA OAEP profiles use MGF1 with the same hash "
            + "as OAEP and an empty label. PKCS1 v1.5 and OAEP with SHA-1 are compatibility choices.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "cipherAttributes is required (may be empty list, but must be present)")
    private List<@NotNull(message = "cipherAttributes must not contain null items") RequestAttribute> cipherAttributes;

    @Schema(description = "Data to be encrypted or decrypted", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "cipherData must contain at least one item")
    @UniqueIdentifiers
    private List<@NotNull(message = "cipherData must not contain null items") @Valid CipherDataV2Dto> cipherData;
}
