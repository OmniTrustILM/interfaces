package com.otilm.api.model.core.cryptoasset;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Data;

/**
 * A PQC verdict recomputed from the asset as stored, rule by rule. The explanation is never written back: the stored
 * verdict stays what the asset detail serves, and {@code matchesStored} says whether the two agree.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CryptographicAssetPqcExplanationDto {

    @Schema(description = "UUID of the inventory asset the explanation is for",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID uuid;

    @Schema(description = "Post-quantum readiness verdict recomputed from the asset as stored",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private PqcVerdict verdict;

    @Schema(description = "The rule that decided the recomputed verdict", requiredMode = Schema.RequiredMode.REQUIRED)
    private String ruleId;

    @Schema(description = "The deciding rule's finding, in the words a stored verdict carries",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String reason;

    @Schema(description = "Values of the asset properties the rules read, as derived from the stored asset. A property "
            + "the asset does not have is omitted; key material and internal deduplication keys are never served",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, Object> inputs;

    @Schema(description = "Every rule the evaluation walked, in evaluation order. Evaluation is first-match-wins: the "
            + "rules before the deciding one are notMatched, the deciding one is decided or resolved, and the rules "
            + "after it are notReached", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<PqcExplanationStepDto> steps;

    @Schema(description = "True when the verdict and rule stored on the asset equal the recomputed ones. False when "
            + "the asset has not been evaluated yet, or its stored verdict predates a change to the asset or to an "
            + "asset it refers to; the stored verdict is served until the next scheduled re-evaluation replaces it",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean matchesStored;

    @Schema(description = "The verdict stored on the asset; absent when the asset has not been evaluated yet",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private PqcVerdict storedVerdict;

    @Schema(description = "The rule that decided the stored verdict; absent when the asset has not been evaluated yet",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String storedRuleId;

    @Schema(description = "When the stored verdict was last evaluated; absent when the asset has not been evaluated "
            + "yet", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private OffsetDateTime storedEvaluatedAt;

    @Schema(description = "When this explanation was computed", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime explainedAt;
}
