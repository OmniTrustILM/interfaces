package com.otilm.api.model.connector.cryptography.operations;

import com.otilm.api.model.connector.cryptography.operations.data.SignatureRequestData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class VerifyDataRequestDto extends SignDataRequestDto {

    @Schema(
            description = "Signatures to verify",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<SignatureRequestData> signatures;

}
