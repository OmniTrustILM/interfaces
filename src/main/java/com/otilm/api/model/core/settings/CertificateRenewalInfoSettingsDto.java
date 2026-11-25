package com.otilm.api.model.core.settings;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Data
public class CertificateRenewalInfoSettingsDto implements Serializable {

    @Schema(description = "Lower bound in seconds for the delay between two refreshes of a certificate's renewal window",
            requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "3600", minimum = "60", maximum = "604800")
    private Integer refreshMinInterval;

    @Schema(description = "Upper bound in seconds for the delay between two refreshes of a certificate's renewal window",
            requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "86400", minimum = "60", maximum = "604800")
    private Integer refreshMaxInterval;

    @Schema(description = "Delay in seconds between refreshes when the CA suggests no retry interval",
            requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "21600", minimum = "60", maximum = "604800")
    private Integer refreshDefaultInterval;

    @Schema(description = "Certificates asked about per request to the connector",
            requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "20", minimum = "1", maximum = "50")
    private Integer refreshBatchSize;

    @Schema(description = "Delay in seconds before Core checks again a certificate for which the CA answered "
            + "`unknown` or `notSupported`", requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "604800",
            minimum = "3600", maximum = "31536000")
    private Integer refreshRecheckInterval;
}
