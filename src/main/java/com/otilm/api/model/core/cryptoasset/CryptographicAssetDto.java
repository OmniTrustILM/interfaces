package com.otilm.api.model.core.cryptoasset;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Data;

/**
 * One row of the cross-CBOM cryptographic asset inventory. A row is a deduplicated asset, not a component: the same
 * algorithm found in many documents is one row, with the references counted on it. The two counts never contradict:
 * {@code occurrenceCount} is at least {@code sourceCbomCount}.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CryptographicAssetDto {

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

    @Schema(description = "Number of occurrences of the asset, summed over its source CBOMs. A source that recorded "
            + "where it found the asset contributes one occurrence per evidence.occurrences entry, counted in full, "
            + "including entries beyond the cap on the detail's per-source evidence list; a source that recorded no "
            + "location counts as one occurrence, the report itself. Never lower than sourceCbomCount, and 0 only when "
            + "sourceCbomCount is 0. Related crypto material with no digest, value or identifier is keyed on its "
            + "occurrence entries (location, line and offset), so such a row stands for one set of entries and its "
            + "occurrences count how often sources reported them, not how many keys or locations exist",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private long occurrenceCount;

    @Schema(description = "True when sources make contradicting claims about this asset that are quarantined "
            + "pending reconciliation", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean quarantined;
}
