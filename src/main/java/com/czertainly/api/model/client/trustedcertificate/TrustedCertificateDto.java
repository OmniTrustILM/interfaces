package com.czertainly.api.model.client.trustedcertificate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TrustedCertificateDto {

    @Schema(
            description = "UUID of the Trusted Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String uuid;

    @Schema(
            description = "Raw certificate content serialized as Base64 String in JSON",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private byte[] certificateContent;

    @Schema(
            description = "Certificate issuer DN",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String issuer;

    @Schema(
            description = "Subject alternative names",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String san;

    @Schema(
            description = "Certificate serial number",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String serialNumber;

    @Schema(
            description = "Certificate subject DN",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String subject;

    @Schema(
            description = "Certificate thumbprint (fingerprint)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String thumbprint;

    @Schema(
            description = "Certificate validity start date",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private LocalDateTime notBefore;

    @Schema(
            description = "Certificate expiration date",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private LocalDateTime notAfter;

}