package com.otilm.api.model.connector.cryptography.v2.operations.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * One item of a V2 encryption or decryption response.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CipherResponseDataV2Dto {

    @Schema(description = "Encrypted or decrypted data. Null when processing the item failed.",
            format = "byte",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @ToString.Exclude
    private byte[] data;

    @Schema(description = "Identifier of the corresponding request item",
            examples = {"customId"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "identifier is required")
    private String identifier;

    @Schema(description = "Additional result details")
    @ToString.Exclude
    private Object details;
}
