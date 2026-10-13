package com.otilm.api.model.common.events.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.core.cryptoasset.CryptographicAssetType;
import com.otilm.api.model.core.cryptoasset.PqcVerdict;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Data;

@Data
public class CryptoAssetAddedEventData implements EventData {

    @Schema(description = "Cryptographic asset UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID cryptoAssetUuid;

    @Schema(description = "CBOM whose ingest created the asset", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID cbomUuid;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Cryptographic asset type. Absent when the producer declared no CycloneDX asset type",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CryptographicAssetType assetType;

    @Schema(description = "Cryptographic asset name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;

    @Schema(description = "Algorithm family", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String algorithmFamily;

    @Schema(description = "Post-quantum readiness verdict", requiredMode = Schema.RequiredMode.REQUIRED)
    private PqcVerdict pqcVerdict;
}
