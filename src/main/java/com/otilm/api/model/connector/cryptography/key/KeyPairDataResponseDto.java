package com.otilm.api.model.connector.cryptography.key;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class KeyPairDataResponseDto {

    @Schema(
            description = "Data of the Public Key",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private KeyDataResponseDto publicKeyData;

    @Schema(
            description = "Data of the Private Key",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private KeyDataResponseDto privateKeyData;

}
