package com.otilm.api.model.connector.cryptography.v2.operations;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.data.CipherRequestDataV2Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Body for {@code POST /v2/cryptographyProvider/operations/encrypt} and {@code .../decrypt}.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class CipherDataRequestV2Dto extends KeyScopedRequestV2Dto {

    @Schema(description = "Cipher attributes",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "cipherAttributes is required (may be empty list, but must be present)")
    private List<RequestAttribute> cipherAttributes;

    @Schema(description = "Data to be encrypted or decrypted",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "cipherData must contain at least one item")
    private List<@NotNull(message = "cipherData must not contain null items") @Valid CipherRequestDataV2Dto>
            cipherData;
}
