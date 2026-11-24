package com.otilm.api.model.core.raprofile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RaProfileRenewalSettingsDto {

    @Schema(description = "Indicator whether a renewal window is computed for certificates of the RA profile",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean windowEnabled;

    @Schema(description = "Start of the renewal window as a percentage of the certificate validity period",
            requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1", maximum = "99")
    private int windowStartPercent;

    @Schema(description = "End of the renewal window as a percentage of the certificate validity period",
            requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1", maximum = "99")
    private int windowEndPercent;

    @Schema(description = "Indicator whether the renewal window suggested by the certificate authority is used when available",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean useAuthorityRenewalInfo;

    @Schema(description = "Indicator whether certificates of the RA profile are renewed automatically when their renewal window starts",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean autoRenewalEnabled;

    @Schema(description = "Number of failed automatic renewals after which a certificate is no longer renewed automatically",
            requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1", maximum = "20")
    private int autoRenewalMaxAttempts;

    @Schema(description = "Indicator whether the authority of the RA profile can suggest renewal windows",
            requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
    private boolean authorityRenewalInfoSupported;
}
