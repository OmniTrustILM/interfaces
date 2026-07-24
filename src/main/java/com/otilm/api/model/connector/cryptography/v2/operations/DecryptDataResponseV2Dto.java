package com.otilm.api.model.connector.cryptography.v2.operations;

import com.otilm.api.model.connector.cryptography.v2.operations.data.CipherResponseDataV2Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Response from {@code POST /v2/cryptographyProvider/operations/decrypt}. Always synchronous.
 */
@Getter
@Setter
@ToString
@Schema(name = "DecryptDataResponseV2Dto")
public class DecryptDataResponseV2Dto {

    @Schema(description = "Decrypted data",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<@Valid CipherResponseDataV2Dto> decryptedData;
}
