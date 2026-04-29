package com.otilm.api.model.client.cryptography.operations;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class VerificationResponseData {

    @Schema(
            description = "Data to be signed or verified",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean result;

    @Schema(
            description = "Custom identifier of the data, that should be the same as in the request, if available",
            examples = {"customId"}
    )
    private String identifier;

    @Schema(
            description = "Additional details for the result, for example reason, etc."
    )
    private Object details;

}
