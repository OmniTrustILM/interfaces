package com.otilm.api.model.core.cryptoasset;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.attribute.v3.content.BaseAttributeContentV3;
import com.otilm.api.model.core.search.AttributeProjectable;
import com.otilm.api.model.core.search.FilterFieldSource;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Data;

/**
 * One row of the cross-CBOM cryptographic asset inventory. A row is a deduplicated asset, not a component: the same
 * algorithm found in many documents is one row, with the references counted on it.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CryptographicAssetDto implements AttributeProjectable {

    @Schema(description = "UUID of the inventory asset", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID uuid;

    @Schema(description = "Normalized display name of the asset: the producers' name, else the recorded OID. Absent "
            + "when neither exists to serve, because a refuted OID is never presented as the name",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;

    @Schema(description = "Type of the asset, as the producer declared it in CycloneDX cryptoProperties.assetType. "
            + "Absent when the producer declared none of the CycloneDX asset types",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CryptographicAssetType type;

    @Schema(description = "Post-quantum readiness verdict computed by the platform rule set",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private PqcVerdict pqcVerdict;

    @Schema(description = "Number of CBOM documents that reference this asset",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private int sourceCbomCount;

    @Schema(description = "Total number of occurrence evidence entries recorded across all source CBOMs",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private long occurrenceCount;

    @Schema(description = "True when sources make contradicting claims about this asset that are quarantined "
            + "pending reconciliation", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean quarantined;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    // No example: the platform registers no custom, metadata or data attributes against this resource, so the
    // shared one - which shows custom values - documents a payload this listing cannot produce.
    @Schema(description = AttributeProjectable.ATTRIBUTE_VALUES_DESCRIPTION,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<FilterFieldSource, Map<String, List<BaseAttributeContentV3<?>>>> attributeValues;
}
