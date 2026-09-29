package com.otilm.api.model.core.cryptoasset;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import java.util.UUID;
import lombok.Data;

/**
 * One rule of the PQC rule set as the explanation walked it.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PqcExplanationStepDto {

    @Schema(description = "The rule this step reports on. A stable identifier, not display text",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String ruleId;

    @Schema(description = "What the rule did for this asset", requiredMode = Schema.RequiredMode.REQUIRED)
    private PqcExplanationStepOutcome outcome;

    @Schema(description = "The verdict the rule yields; present on decided and resolved steps",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private PqcVerdict verdict;

    @Schema(description = "Why the rule did what it did, in plain words", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    @Schema(description = "Values of the asset properties this rule read, a subset of the explanation's inputs; "
            + "absent on a step that was not reached", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<String, Object> evaluatedFields;

    @Schema(description = "On a resolved step, the inventory asset whose own verdict this rule carried over, such as "
            + "the key a certificate certifies or the weakest algorithm a protocol's cipher suites name",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID referencedAssetUuid;
}
