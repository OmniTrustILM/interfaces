package com.otilm.api.model.core.settings;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.io.Serializable;

@Data
public class CertificateRegistrationSettingsUpdateDto implements Serializable {

    @Schema(description = "Default issuance window in days, applied when a pre-registration omits an explicit expiry", requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "7", minimum = "1")
    @Positive
    private Integer defaultIssuanceWindowDays = 7;

    @Schema(description = "Maximum failed challenge-verification attempts before the registration authorization locks", requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "5", minimum = "1")
    @Positive
    private Integer maxFailedAttempts = 5;

}
