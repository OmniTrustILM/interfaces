package com.otilm.api.model.core.certificate;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Data;

@Data
public class CertificateRenewalInfoDto {

    @Schema(description = "Start of the suggested renewal window", requiredMode = Schema.RequiredMode.REQUIRED)
    private Date windowStart;

    @Schema(description = "End of the suggested renewal window", requiredMode = Schema.RequiredMode.REQUIRED)
    private Date windowEnd;

    @Schema(description = "URL with an explanation of the renewal window, provided by the certificate authority",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String explanationUrl;

    @Schema(description = "Origin of the renewal window", requiredMode = Schema.RequiredMode.REQUIRED)
    private RenewalWindowSource source;

    @Schema(description = "Time the renewal window was last updated", requiredMode = Schema.RequiredMode.REQUIRED)
    private Date updatedAt;

    @Schema(description = "Indicates whether the renewal window has started, so the certificate is due for renewal",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean due;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Automatic renewal of the certificate; absent when automatic renewal is not enabled for it",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CertificateAutoRenewalDto autoRenewal;
}
