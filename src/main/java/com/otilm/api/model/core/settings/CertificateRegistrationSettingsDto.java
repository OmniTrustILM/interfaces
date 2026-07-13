package com.otilm.api.model.core.settings;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CertificateRegistrationSettingsDto implements Serializable {

    @Schema(description = "Default issuance window in days, applied when a pre-registration omits an explicit expiry", requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "1")
    private Integer defaultIssuanceWindowDays;

    @Schema(description = "Maximum failed challenge-verification attempts before the registration authorization locks", requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "1")
    private Integer maxFailedAttempts;

}
