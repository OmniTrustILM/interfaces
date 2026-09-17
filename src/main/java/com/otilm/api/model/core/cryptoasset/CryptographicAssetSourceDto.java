package com.otilm.api.model.core.cryptoasset;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Data;

/**
 * One source CBOM document referencing an inventory asset. The payload keeps what that source declared rather than a
 * normalized projection, because sources disagree and the disagreement is the evidence an operator traces.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CryptographicAssetSourceDto {

    @Schema(description = "UUID of the source CBOM record", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID cbomUuid;

    @Schema(description = "Serial number (URN) of the source CBOM", requiredMode = Schema.RequiredMode.REQUIRED)
    private String serialNumber;

    @Schema(description = "Version of the source CBOM", requiredMode = Schema.RequiredMode.REQUIRED)
    private int version;

    @Schema(description = "Number of occurrences this source recorded for the asset, counted before the served "
            + "evidence list is capped", requiredMode = Schema.RequiredMode.REQUIRED)
    private long occurrenceCount;

    @Schema(description = "Tool or scan that produced the source CBOM (e.g.: CBOM-Lens); absent when the document's "
            + "metadata component carries no name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String source;

    @Schema(description = "Cryptographic properties of the component as this source declared them. Keys follow the "
            + "source document's CycloneDX version and are always source-derived — the platform adds no keys of its "
            + "own. Values classified as secret material are stored and served as the redaction envelope "
            + "{\"redacted\": true, \"length\": n} with no digest, and a value whose classification is absent is "
            + "treated as secret.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<String, Object> payload;

    @Schema(description = "Occurrence evidence recorded from this source, sorted deterministically and capped; "
            + "occurrenceCount carries the pre-cap total. May be empty",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<CryptographicAssetEvidenceDto> evidence;
}
