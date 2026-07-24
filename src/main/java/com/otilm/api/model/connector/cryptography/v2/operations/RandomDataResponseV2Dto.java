package com.otilm.api.model.connector.cryptography.v2.operations;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Response from {@code POST /v2/cryptographyProvider/operations/random}. Always synchronous.
 */
@Getter
@Setter
@ToString
@Schema(name = "RandomDataResponseV2Dto")
public class RandomDataResponseV2Dto {

    @Schema(description = "Random generated data",
            format = "byte",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @ToString.Exclude
    private byte[] data;
}
