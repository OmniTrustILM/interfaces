package com.otilm.api.model.connector.cryptography.v2.operations.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.connector.common.v2.OperationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Per-item outcome of an asynchronous sign batch, correlated to its request by {@code identifier}.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SignatureItemResultV2Dto {

    @Schema(description = "Identifier of the corresponding item in the original sign request",
            examples = {"customId"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String identifier;

    @Schema(description = "Status of this item",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private OperationStatus status;

    @Schema(description = "Signature over the item's data. Populated when status=COMPLETED.",
            format = "byte",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @ToString.Exclude
    private byte[] signature;

    @Schema(description = "Failure detail when status=FAILED — curated message text (no raw exception messages)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String reason;
}
