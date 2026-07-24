package com.otilm.api.model.connector.cryptography.v2.operations;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.data.SignatureRequestDataV2Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Body for {@code POST /v2/cryptographyProvider/operations/verify}.
 * Signed data and signatures are correlated by identifier.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class VerifyDataRequestV2Dto extends KeyScopedRequestV2Dto {

    @Schema(description = "Signature attributes",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "signatureAttributes is required (may be empty list, but must be present)")
    private List<RequestAttribute> signatureAttributes;

    @Schema(description = "Signed data, correlated to the signatures by identifier",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "data must contain at least one item")
    private List<@NotNull(message = "data must not contain null items") @Valid SignatureRequestDataV2Dto> data;

    @Schema(description = "Signatures to verify, correlated to the signed data by identifier",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "signatures must contain at least one item")
    private List<@NotNull(message = "signatures must not contain null items") @Valid SignatureRequestDataV2Dto>
            signatures;
}
