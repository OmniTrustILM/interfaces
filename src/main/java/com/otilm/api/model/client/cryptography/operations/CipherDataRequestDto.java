package com.otilm.api.model.client.cryptography.operations;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CipherDataRequestDto {

    @Schema(
            description = "List of cipher Attributes",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute>cipherAttributes;

    @Schema(
            description = "Encrypted/decrypted data",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<CipherRequestData> cipherData;

}
