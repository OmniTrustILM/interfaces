package com.otilm.api.model.core.discovery;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    @Schema(description = "Whether the certificate was new to the inventory when this discovery found it",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean newlyDiscovered;

    @Schema(description = "Whether processing of the certificate has been attempted; processedError gives the "
            + "outcome.", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean processed;

    @Schema(description = "A reason recorded against the certificate, not necessarily a failure: an imported "
            + "certificate can carry one (its keys not all associated), and so can one never attempted.")
    private String processedError;

}
