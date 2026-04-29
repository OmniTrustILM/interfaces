package com.otilm.api.model.connector.cryptography.operations.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SignatureRequestData {

    @Schema(
            description = "Data to be signed or verified",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private byte[] data;

    @Schema(
            description = "Custom identifier of the data, that should be the same as in the request, if available",
            examples = {"customId"}
    )
    private String identifier;

}
