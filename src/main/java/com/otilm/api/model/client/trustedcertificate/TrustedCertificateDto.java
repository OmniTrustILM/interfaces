package com.otilm.api.model.client.trustedcertificate;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TrustedCertificateDto {

    @Schema(description = "UUID of the Trusted Certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    private String uuid;

    @Schema(description = "UUID of the inventory certificate that is trusted",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String certificateUuid;

    @Schema(description = "Raw certificate content serialized as Base64 String in JSON",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private byte[] certificateContent;

    @Schema(description = "Certificate issuer DN", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String issuer;

    @Schema(description = "Subject alternative names", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String san;

    @Schema(description = "Certificate serial number", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String serialNumber;

    @Schema(description = "Certificate subject DN", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String subject;

    @Schema(description = "Certificate thumbprint (fingerprint)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String thumbprint;

    @Schema(description = "Certificate validity start date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime notBefore;

    @Schema(description = "Certificate expiration date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime notAfter;

    @Schema(description = "Purposes the certificate is trusted for; empty only while a platform removal is pending",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Set<TrustedCertificatePurpose> purposes;

    @Schema(description = "Present when the platform applies trust and the certificate has the platform purpose or "
            + "a pending platform removal", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private TrustedCertificatePlatformState platformState;

    @Schema(description = "Reason for a failed or drifted platform state",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String platformStateMessage;

    @Schema(description = "When the platform state was last checked", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime platformStateCheckedAt;

    @Schema(description = "Expires within the validation expiring threshold, 30 days when not set",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean expiring;

}
