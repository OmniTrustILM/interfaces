package com.otilm.api.model.connector.signatures.signer;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProxySignatureValidateResponseDto {

    @Schema(
            description = "True if the signature is valid",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean valid;

    @Schema(
            description = "Human-readable summary or error reason",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String message;

    @Schema(
            description = "Optional arbitrary validation report from the external service (XML, JSON, PDF, ...). " +
                    "Must be set together with resultContentType.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private byte[] result;

    @Schema(
            description = "MIME type of result. Must be set if and only if result is set.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String resultContentType;

}
