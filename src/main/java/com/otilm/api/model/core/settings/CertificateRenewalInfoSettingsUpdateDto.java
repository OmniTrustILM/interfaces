package com.otilm.api.model.core.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

@Data
public class CertificateRenewalInfoSettingsUpdateDto implements Serializable {

    @Schema(description = "Lower bound in seconds for the delay between two refreshes of a certificate's renewal window",
            requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "3600", minimum = "60", maximum = "604800")
    @NotNull
    @Min(60)
    @Max(604800)
    private Integer refreshMinInterval = 3600;

    @Schema(description = "Upper bound in seconds for the delay between two refreshes of a certificate's renewal window",
            requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "86400", minimum = "60", maximum = "604800")
    @NotNull
    @Min(60)
    @Max(604800)
    private Integer refreshMaxInterval = 86400;

    @Schema(description = "Delay in seconds between refreshes when the CA gives no retry-after",
            requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "21600", minimum = "60", maximum = "604800")
    @NotNull
    @Min(60)
    @Max(604800)
    private Integer refreshDefaultInterval = 21600;

    @Schema(description = "Certificates asked about per request to the connector",
            requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "20", minimum = "1", maximum = "50")
    @NotNull
    @Min(1)
    @Max(50)
    private Integer refreshBatchSize = 20;

    @Schema(description = "Delay in seconds before certificates the CA answered unknown or notSupported for are "
            + "checked again", requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "604800", minimum = "3600",
            maximum = "31536000")
    @NotNull
    @Min(3600)
    @Max(31536000)
    private Integer refreshRecheckInterval = 604800;

    @AssertTrue(message = "Minimum interval must not exceed the default interval, which must not exceed the maximum.")
    @JsonIgnore
    @Schema(hidden = true)
    public boolean isIntervalOrderValid() {
        return refreshMinInterval == null || refreshDefaultInterval == null || refreshMaxInterval == null
                || (refreshMinInterval <= refreshDefaultInterval && refreshDefaultInterval <= refreshMaxInterval);
    }
}
