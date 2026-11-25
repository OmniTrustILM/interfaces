package com.otilm.api.model.connector.v3.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Renewal advice for one certificate of a /renewalInfo batch.
 */
@Getter
@Setter
@ToString
public class CertificateRenewalInfoResultDtoV3 {

    @Schema(description = "Reference of the request item this result answers",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String reference;

    @Schema(description = "Outcome for the certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    private CertificateRenewalInfoStatus status;

    @Schema(description = "Start of the window in which the CA advises renewing. Required when `status` is "
            + "`available`. A start in the past means renew now.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private OffsetDateTime windowStart;

    @Schema(description = "End of the renewal window, after `windowStart`. Required when `status` is `available`.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private OffsetDateTime windowEnd;

    @Schema(description = "URL of a page explaining the CA's advice", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String explanationUrl;

    @Schema(description = "Seconds until Core should ask again for this certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "0")
    @Min(0)
    private Integer retryAfter;

    @Schema(description = "Human-readable explanation of the outcome. Must not contain raw internal error messages.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String reason;
}
