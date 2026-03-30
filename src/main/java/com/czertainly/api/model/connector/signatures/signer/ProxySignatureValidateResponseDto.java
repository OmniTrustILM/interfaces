package com.czertainly.api.model.connector.signatures.signer;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
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
