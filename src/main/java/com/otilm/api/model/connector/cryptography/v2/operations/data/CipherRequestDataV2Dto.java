package com.otilm.api.model.connector.cryptography.v2.operations.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * One item of a V2 encryption or decryption request.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CipherRequestDataV2Dto {

    @Schema(description = "Data to be encrypted or decrypted",
            format = "byte",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @ToString.Exclude
    private byte[] data;

    @Schema(description = "Caller-assigned identifier of the item",
            examples = {"customId"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "identifier is required")
    private String identifier;
}
