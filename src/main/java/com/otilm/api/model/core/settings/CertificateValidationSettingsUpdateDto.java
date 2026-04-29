package com.otilm.api.model.core.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.io.Serializable;

@Data
public class CertificateValidationSettingsUpdateDto implements Serializable {

    @Schema(description = "Indicator whether validation of certificates should be enabled", requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "true")
    private boolean enabled = true;

    @Schema(description = "Frequency of validation of certificates in days", requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "1", minimum = "1")
    @Positive
    private Integer frequency = 1;

    @Schema(description = "How many days before expiration should certificate validation status change to Expiring", requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "30", minimum = "1")
    @Positive
    private Integer expiringThreshold = 30;

    @AssertTrue(message = "Frequency and expiring threshold values must not be null for enabled validation.")
    @JsonIgnore
    public boolean isValid() {
        return !enabled || (frequency != null && expiringThreshold != null);
    }

}
