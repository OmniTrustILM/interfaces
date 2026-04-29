package com.otilm.api.model.connector.cryptography.operations;

import com.otilm.api.model.connector.cryptography.operations.data.CipherResponseData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class EncryptDataResponseDto {

    @Schema(
            description = "Encrypted data",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<CipherResponseData> encryptedData;

}
