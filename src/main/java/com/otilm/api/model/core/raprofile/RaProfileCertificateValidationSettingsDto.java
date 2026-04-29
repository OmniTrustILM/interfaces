package com.otilm.api.model.core.raprofile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RaProfileCertificateValidationSettingsDto {

    @Schema(description = "Indicator whether validation of certificates associated with RA profile should be enabled, if null, then values in platform settings will be used", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean enabled;

    @Schema(description = "Frequency of validation of certificates in days", requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "1")
    private Integer frequency;

    @Schema(description = "How many days before expiration should certificate validation status change to Expiring", requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "1")
    private Integer expiringThreshold;

}
