package com.otilm.api.model.client.trustedcertificate;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "What the platform trusts and whether ILM manages it")
public class TrustedCertificatePlatformDto {

    @Schema(description = "Whether ILM applies platform trust in this deployment",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean managed;

    @Schema(description = "Where platform trust comes from", requiredMode = Schema.RequiredMode.REQUIRED)
    private TrustedCertificatePlatformSource source;

    @Schema(description = "Certificates of the deployment bundle; empty unless the source is deployment, since "
            + "managed platform trust is listed with the trusted certificates",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<PlatformBundleCertificateDto> certificates;

    @Schema(description = "Health of the comparison with the platform; present only when the source is provisioning",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private TrustedCertificateSyncDto sync;

}
