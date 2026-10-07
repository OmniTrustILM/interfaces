package com.otilm.api.model.connector.cryptography.v2.operations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.data.IdentifiedDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.data.SignatureDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.validation.UniqueIdentifiers;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Body for {@code POST /v2/cryptographyProvider/operations/verify}. Signed data and signatures are correlated by
 * identifier. The batch selects one signature algorithm through the reserved signatureAlgorithm attribute.
 */
@Getter
@Setter
@ToString(callSuper = true)
@Schema(name = "VerifyDataRequestV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class VerifyDataRequestV2Dto extends KeyScopedRequestV2Dto {

    @Schema(description = "Batch-wide signature settings. Must include exactly one v3 string attribute named "
            + "signatureAlgorithm with UUID 9180267f-c82f-4b7b-8160-d2363d813869, selecting exactly one "
            + "signature algorithm code offered by the verification attribute schema.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "signatureAttributes is required")
    private List<@NotNull(
            message = "signatureAttributes must not contain null items") RequestAttribute> signatureAttributes;

    @Schema(description = "Signed data, correlated to the signatures by identifier",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "data must contain at least one item")
    @UniqueIdentifiers
    private List<@NotNull(message = "data must not contain null items") @Valid SignatureDataV2Dto> data;

    @Schema(description = "Signatures to verify, correlated to the signed data by identifier",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "signatures must contain at least one item")
    @UniqueIdentifiers
    private List<@NotNull(message = "signatures must not contain null items") @Valid SignatureDataV2Dto> signatures;

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "signature identifiers must exactly match signed-data identifiers")
    public boolean isVerificationIdentifiersMatching() {
        if (containsInvalidIdentifier(data) || containsInvalidIdentifier(signatures)) {
            return true;
        }

        return data.size() == signatures.size() && identifiers(data).equals(identifiers(signatures));
    }

    private static boolean containsInvalidIdentifier(List<? extends IdentifiedDataV2Dto> items) {
        return items == null || items
                .stream()
                .anyMatch(item -> item == null || item.getIdentifier() == null || item.getIdentifier().isBlank());
    }

    private static Set<String> identifiers(List<? extends IdentifiedDataV2Dto> items) {
        return items.stream().map(IdentifiedDataV2Dto::getIdentifier).collect(Collectors.toSet());
    }
}
