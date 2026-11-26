package com.otilm.api.model.client.trustedcertificate.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Certificate of the deployment's platform trust bundle")
public class PlatformBundleCertificateDto {

    @Schema(description = "Certificate subject DN", requiredMode = Schema.RequiredMode.REQUIRED)
    private String subject;

    @Schema(description = "Certificate issuer DN", requiredMode = Schema.RequiredMode.REQUIRED)
    private String issuer;

    @Schema(description = "Certificate serial number", requiredMode = Schema.RequiredMode.REQUIRED)
    private String serialNumber;

    @Schema(description = "Certificate SHA-256 fingerprint", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fingerprint;

    @Schema(description = "Certificate validity start date", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime notBefore;

    @Schema(description = "Certificate expiration date", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime notAfter;

    @Schema(description = "Expires within the validation expiring threshold, 30 days when not set",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean expiring;

}
