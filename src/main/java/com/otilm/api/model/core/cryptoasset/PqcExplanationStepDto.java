package com.otilm.api.model.core.cryptoasset;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.Data;

/**
 * One rule of the PQC rule set as the explanation walked it.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "One rule of the PQC rule set as the explanation evaluated it. verdict is present on matched, "
        + "decided, resolved and failed steps and absent on a notMatched one; evaluatedFields is present on every "
        + "evaluated step; referencedAsset is present on a resolved step, and on a matched step whose rule carried "
        + "another asset's verdict")
public class PqcExplanationStepDto {

    @Schema(description = "The rule this step reports on. A stable identifier, not display text. Every rule is listed "
            + "under a fixed id, the name-based and family rules included. The one exception is the hybrid rule, "
            + "whose id is composed during evaluation: it is listed as PQC-HYBRID on a step it does not decide, and "
            + "as PQC-HYBRID- followed by the deciding family's rule id on the step it decides",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String ruleId;

    @Schema(description = "What the rule checks, in a few words for display beside the rule id, such as \"Certified "
            + "key\" or \"Symmetric key size\". The same for every asset that walks the rule",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "What the rule did for this asset", requiredMode = Schema.RequiredMode.REQUIRED)
    private PqcExplanationStepOutcome outcome;

    @Schema(description = "The verdict the rule yields", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private PqcVerdict verdict;

    @Schema(description = "Why the rule did what it did, in plain words", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    @Schema(description = "Values of the asset properties this rule read and, on a resolved step, the reference it "
            + "decided by. Values are strings, numbers, booleans or lists of strings, never nested objects",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<String, Object> evaluatedFields;

    @Schema(description = "The inventory asset whose stored verdict this rule carried over",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private PqcReferencedAssetDto referencedAsset;
}
