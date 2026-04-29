package com.otilm.api.model.connector.cryptography.operations;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RandomDataResponseDto {

    @Schema(
            description = "Random generated data",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private byte[] data;

}
