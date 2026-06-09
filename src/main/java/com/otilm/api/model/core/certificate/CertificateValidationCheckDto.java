package com.otilm.api.model.core.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateValidationCheckDto {

    @Schema(
            description = "Certificate validation check type",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private CertificateValidationCheck validationCheck;

    @Schema(
            description = "Certificate validation check result status",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private CertificateValidationStatus status;

    @Schema(
            description = "Certificate validation check result message",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String message;
}
