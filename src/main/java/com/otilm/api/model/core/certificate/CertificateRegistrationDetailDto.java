package com.otilm.api.model.core.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
public class CertificateRegistrationDetailDto implements Serializable {

    @Schema(description = "Registration authorization state", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CertificateRegistrationState state;

    @Schema(description = "Issuance deadline for completing the pre-registered certificate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private OffsetDateTime expiresAt;

    @Schema(description = "Number of failed challenge-verification attempts recorded", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer failedAttempts;

}
