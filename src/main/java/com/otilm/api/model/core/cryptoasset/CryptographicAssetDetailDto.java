package com.otilm.api.model.core.cryptoasset;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Detail of one inventory asset: the row fields plus verdict provenance, every source CBOM that references the asset
 * with the payload and occurrence evidence recorded from it, and the producer OIDs including refuted ones. The
 * per-source originals are served beside the elected representative payload; the two are never merged.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CryptographicAssetDetailDto extends CryptographicAssetDto {

    @Schema(description = "Provenance of the asset's PQC verdict. Absent until the first rule-set evaluation of "
            + "this asset; the row-level pqcVerdict serves unknown until then",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CryptographicAssetVerdictDto verdict;

    @Schema(description = "Normalized filterable properties derived for the asset; absent when nothing is derivable",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CryptographicAssetNormalizedFieldsDto normalizedFields;

    @Schema(description = "The representative payload: one source's payload served verbatim, elected "
            + "deterministically (richest payload first, ties broken on a canonical form of the payload) and never "
            + "merged across sources. Absent when no source carries a payload",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<String, Object> electedPayload;

    @Schema(description = "Source CBOM documents referencing this asset, with per-source payload and evidence",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<CryptographicAssetSourceDto> sources;

    @Schema(description = "Object identifiers producers attached to this asset; may be empty",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<CryptographicAssetOidDto> oids;
}
