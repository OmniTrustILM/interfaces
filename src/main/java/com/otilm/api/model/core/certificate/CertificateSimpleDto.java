package com.otilm.api.model.core.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class CertificateSimpleDto {

    @Schema(description = "UUID of the certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID uuid;

    @Schema(description = "Type of the certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    private CertificateType certificateType;

    @Schema(description = "State of the certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    private CertificateState state;

    @Schema(description = "Relation type", requiredMode = Schema.RequiredMode.REQUIRED)
    private CertificateRelationType relationType;

    @Schema(description = "Common name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String commonName;

    @Schema(description = "Subject distinguished name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String subjectDn;

    @Schema(description = "Issuer common name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String issuerCommonName;

    @Schema(description = "Issuer distinguished name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String issuerDn;

    @Schema(description = "Certificate serial number", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String serialNumber;

    @Schema(description = "Certificate fingerprint", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String fingerprint;

    @Schema(description = "Public key algorithm", requiredMode = Schema.RequiredMode.REQUIRED)
    private String publicKeyAlgorithm;

    @Schema(description = "Alternative public key algorithm", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String altPublicKeyAlgorithm;

    @Schema(description = "Signature algorithm", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String signatureAlgorithm;

    @Schema(description = "Alternative signature algorithm", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String altSignatureAlgorithm;

    @Schema(description = "Not before date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Date notBefore;

    @Schema(description = "Not after date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Date notAfter;
}

