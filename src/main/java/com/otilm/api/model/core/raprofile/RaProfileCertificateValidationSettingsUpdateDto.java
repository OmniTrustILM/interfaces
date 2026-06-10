package com.otilm.api.model.core.raprofile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RaProfileCertificateValidationSettingsUpdateDto {

    @Schema(description = "Indicator whether validation of certificates associated with RA profile should be enabled, if null, then values in platform settings will be used" , requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean enabled;

    @Schema(description = "Frequency of validation of certificates in days, when not set, value in platform settings is used", requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "1", defaultValue = "1")
    @Positive
    private Integer frequency = 1;

    @Schema(description = "How many days before expiration should certificate validation status change to Expiring, when not set, value in platform settings is used", requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "1", defaultValue = "30")
    @Positive
    private Integer expiringThreshold = 30;

    @AssertTrue(message = "Frequency and expiring threshold values must not be null for enabled validation.")
    @JsonIgnore
    public boolean isValid() {
        return enabled == null || !enabled || (frequency != null && expiringThreshold != null);
    }

}
