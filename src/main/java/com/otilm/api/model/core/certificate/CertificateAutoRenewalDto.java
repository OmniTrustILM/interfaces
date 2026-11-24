package com.otilm.api.model.core.certificate;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CertificateAutoRenewalDto {

    @Schema(description = "How the certificate is renewed automatically", requiredMode = Schema.RequiredMode.REQUIRED)
    private AutoRenewalMode mode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Reason the certificate cannot be renewed automatically; present when the mode is none",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String blockedReason;
}
