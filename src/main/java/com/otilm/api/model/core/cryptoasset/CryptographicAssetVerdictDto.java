package com.otilm.api.model.core.cryptoasset;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.Data;

/**
 * Provenance of an asset's PQC verdict. The verdict value itself is the {@code pqcVerdict} carried on the asset row;
 * this block records which rule decided it, when, and from what, so a verdict can be re-examined when the rule set
 * moves on. Evaluation is first-match-wins, so the deciding rule is singular. A re-evaluation that leaves the verdict
 * unchanged advances {@code evaluatedAt} but not {@code decidedAt}.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CryptographicAssetVerdictDto {

    @Schema(description = "Version of the platform rule set that produced the verdict",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private int ruleSetVersion;

    @Schema(description = "The rule that decided the verdict; absent when no rule matched and the verdict is the "
            + "rule set's default", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String ruleId;

    @Schema(description = "The deciding rule's finding; absent when no rule matched",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String reason;

    @Schema(description = "Values of the asset fields the deciding rule evaluated, recorded at decision time "
            + "and as stored, so assetType may be unroutable for an asset served with no type",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<String, Object> evaluatedFields;

    @Schema(description = "When the current verdict value was decided", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime decidedAt;

    @Schema(description = "When the verdict was last evaluated; a re-evaluation that confirms the verdict advances "
            + "this and not decidedAt", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime evaluatedAt;
}
