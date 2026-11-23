package com.otilm.api.model.core.certificate;

import com.otilm.api.model.client.trustedcertificate.TrustedCertificatePlatformState;
import com.otilm.api.model.client.trustedcertificate.TrustedCertificatePurpose;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Trust of a certificate")
public class CertificateTrustDto {

    @Schema(description = "UUID of the Trusted Certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    private String trustedCertificateUuid;

    @Schema(description = "Purposes the certificate is trusted for; empty only while a platform removal is pending",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Set<TrustedCertificatePurpose> purposes;

    @Schema(description = "Present when ILM manages platform trust and the certificate has the platform purpose or "
            + "a pending platform removal", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private TrustedCertificatePlatformState platformState;

}
