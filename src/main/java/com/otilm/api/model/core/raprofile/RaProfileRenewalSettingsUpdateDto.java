package com.otilm.api.model.core.raprofile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class RaProfileRenewalSettingsUpdateDto {

    @Schema(description = "Indicator whether a renewal window is computed for certificates of the RA profile",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "false")
    private boolean windowEnabled = false;

    @Schema(description = "Start of the renewal window as a percentage of the certificate validity period; must be lower than the end",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "1", maximum = "99", defaultValue = "67")
    @Min(1)
    @Max(99)
    private int windowStartPercent = 67;

    @Schema(description = "End of the renewal window as a percentage of the certificate validity period; must be higher than the start",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "1", maximum = "99", defaultValue = "80")
    @Min(1)
    @Max(99)
    private int windowEndPercent = 80;

    @Schema(description = "Indicator whether the renewal window suggested by the certificate authority is used when available",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "false")
    private boolean useAuthorityRenewalInfo = false;

    @Schema(description = "Indicator whether certificates of the RA profile are renewed automatically when their renewal window starts",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "false")
    private boolean autoRenewalEnabled = false;

    @Schema(description = "Number of failed automatic renewals after which a certificate is no longer renewed automatically",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "1", maximum = "20", defaultValue = "5")
    @Min(1)
    @Max(20)
    private int autoRenewalMaxAttempts = 5;

    @AssertTrue(message = "Renewal window start percentage must be lower than the end percentage.")
    @JsonIgnore
    public boolean isValid() {
        return windowStartPercent < windowEndPercent;
    }
}
