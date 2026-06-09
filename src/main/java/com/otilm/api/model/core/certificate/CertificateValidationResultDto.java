package com.otilm.api.model.core.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateValidationResultDto {

    @Schema(
            description = "Overall certificate validation result status",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private CertificateValidationStatus resultStatus;

    @Schema(
            description = "Certificate validation check results",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Map<CertificateValidationCheck, CertificateValidationCheckDto> validationChecks;

    @Schema(
            description = "Overall certificate validation result message", requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String message;

    @Schema(
            description = "Date of the most recent validation of the certificate", requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private OffsetDateTime validationTimestamp;
}
