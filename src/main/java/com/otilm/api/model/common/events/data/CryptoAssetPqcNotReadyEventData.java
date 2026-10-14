package com.otilm.api.model.common.events.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.core.cryptoasset.CryptographicAssetType;
import com.otilm.api.model.core.cryptoasset.PqcVerdict;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CryptoAssetPqcNotReadyEventData implements EventData {

    @Schema(description = "Cryptographic asset UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private UUID cryptoAssetUuid;

    @Schema(description = "Cryptographic asset type. Absent when the producer declared no CycloneDX asset type",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CryptographicAssetType assetType;

    @Schema(description = "Cryptographic asset name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;

    @Schema(description = "Algorithm family", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String algorithmFamily;

    @Schema(description = "Rule that decided the asset is not PQC ready",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String pqcRuleId;

    @Schema(description = "Finding from the rule that decided the asset is not PQC ready",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String pqcReason;

    @Schema(description = "Last decided verdict before this one: READY or NOT_APPLICABLE. An UNKNOWN result is not "
            + "recorded, so an UNKNOWN in between keeps the earlier decided verdict. Absent when the asset had no decided "
            + "verdict before", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private PqcVerdict previousVerdict;
}
