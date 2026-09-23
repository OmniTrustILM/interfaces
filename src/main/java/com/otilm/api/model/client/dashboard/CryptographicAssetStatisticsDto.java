package com.otilm.api.model.client.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.Data;

/**
 * Statistics for the cross-CBOM cryptographic asset inventory dashboard. Every count is computed over deduplicated
 * inventory assets, not raw CBOM components, so an asset referenced by many documents counts once. The
 * sync-completeness block states how much of the stored estate the numbers cover — without it a partially synced estate
 * would present partial numbers as the whole truth.
 */
@Data
@Schema(name = "CryptographicAssetStatisticsDto", description = "Cryptographic asset inventory dashboard statistics")
public class CryptographicAssetStatisticsDto {

    @Schema(description = "Total number of deduplicated cryptographic assets in the inventory")
    private Long totalAssets;

    @Schema(description = "Number of CBOM documents that contributed at least one asset to the inventory; "
            + "documents whose assets are not yet synced do not count")
    private Long sourceCbomCount;

    @Schema(description = "Asset count by asset type. Keys are the four CycloneDX asset type codes; every one is "
            + "present, with 0 when none. Assets with no asset type are counted in untypedAssetCount instead")
    private Map<String, Long> statByType;

    @Schema(description = "Number of assets served with no asset type, because their producer declared none of the "
            + "CycloneDX asset types. Never counted in statByType")
    private Long untypedAssetCount;

    @Schema(description = "Asset count by PQC readiness verdict. Keys are PqcVerdict codes; every verdict is "
            + "present, with 0 when none")
    private Map<String, Long> statByPqcVerdict;

    @Schema(description = "Asset count by algorithm family, limited to the top-N families by count. Use "
            + "distinctAlgorithmFamilyCount for the total number of families; assets with no family are counted in "
            + "unassignedAssetCount and never occupy a slot here")
    private Map<String, Long> statByAlgorithmFamily;

    @Schema(description = "Number of distinct algorithm families across the inventory, excluding assets with no "
            + "family. Lets the client render a \"+k more\" overflow beyond the top-N entries in "
            + "statByAlgorithmFamily")
    private Long distinctAlgorithmFamilyCount;

    @Schema(description = "Number of assets with no algorithm family, which is common: the family concept does not "
            + "apply to most related-crypto-material. Never counted in statByAlgorithmFamily or "
            + "distinctAlgorithmFamilyCount")
    private Long unassignedAssetCount;

    @Schema(description = "How completely the inventory reflects the stored CBOM documents")
    private CryptographicAssetSyncCompletenessDto syncCompleteness;
}
