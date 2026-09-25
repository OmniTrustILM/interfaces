package com.otilm.api.model.core.cryptoasset;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Data;

/**
 * One row of the cross-CBOM cryptographic asset inventory. A row is a deduplicated asset, not a component: the same
 * algorithm found in many documents is one row, with the references counted on it. The two counts never contradict:
 * {@code sightingCount} is at least {@code sourceCbomCount}.
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

    @Schema(description = "Number of times the asset was sighted, summed over its source CBOMs. A source that recorded "
            + "where it found the asset contributes one sighting per evidence.occurrences entry, counted before the "
            + "served evidence list is capped; a source that recorded no location contributes one sighting, the report "
            + "itself. Never lower than sourceCbomCount, and 0 only when sourceCbomCount is 0. Related crypto material "
            + "that its producers described only by name and location is tracked per location, so for such a row the "
            + "sightings are the locations the row stands for", requiredMode = Schema.RequiredMode.REQUIRED)
    private long sightingCount;

    @Schema(description = "True when sources make contradicting claims about this asset that are quarantined "
            + "pending reconciliation", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean quarantined;
}
