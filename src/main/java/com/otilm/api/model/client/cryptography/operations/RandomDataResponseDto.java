package com.otilm.api.model.client.cryptography.operations;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RandomDataResponseDto {

    @Schema(
            description = "Base64 encoded random data",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String data;

}
