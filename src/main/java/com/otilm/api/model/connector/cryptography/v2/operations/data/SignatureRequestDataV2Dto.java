package com.otilm.api.model.connector.cryptography.v2.operations.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * One item of a V2 sign or verify batch. Results are correlated to request items by {@code identifier}.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignatureRequestDataV2Dto {

    @Schema(description = "Data to be signed or verified",
            format = "byte",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "data is required")
    @ToString.Exclude
    private byte[] data;

    @Schema(description = "Caller-assigned identifier of the item, unique within the batch. Per-item results are "
            + "mapped back to their request by this value.",
            examples = {"customId"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "identifier is required and must be unique within the batch")
    private String identifier;
}
