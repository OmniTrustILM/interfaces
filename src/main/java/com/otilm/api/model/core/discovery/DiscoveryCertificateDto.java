package com.otilm.api.model.core.discovery;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class DiscoveryCertificateDto {
    @Schema(description = "UUID of the Certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    private String uuid;

    @Schema(description = "UUID of the Certificate in Certificate inventory")
    private String inventoryUuid;

    @Schema(description = "Certificate common name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String commonName;

    @Schema(description = "Certificate Serial Number", requiredMode = Schema.RequiredMode.REQUIRED)
    private String serialNumber;

    @Schema(description = "Issuer common name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String issuerCommonName;

    @Schema(description = "Certificate validity start date", requiredMode = Schema.RequiredMode.REQUIRED)
    private Date notBefore;

    @Schema(description = "Certificate expiration date", requiredMode = Schema.RequiredMode.REQUIRED)
    private Date notAfter;

    @Schema(description = "SHA256 thumbprint of the certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fingerprint;

    @Schema(description = "Base64 encoded Certificate content", requiredMode = Schema.RequiredMode.REQUIRED)
    private String certificateContent;

    @Schema(description = "Boolean representing if the certificate is newly discovered. True - Certificate is newly discovered" +
            "false - Certificate was already available in the inventory",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean newlyDiscovered;

    @Schema(description = "Indicator whether the discovery certificate has already been processed.", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean processed;

    @Schema(description = "Error message in case of failed processing of the discovery certificate.")
    private String processedError;

}
