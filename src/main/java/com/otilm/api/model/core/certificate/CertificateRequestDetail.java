package com.otilm.api.model.core.certificate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.core.compliance.ComplianceStatus;
import com.otilm.api.model.core.enums.CertificateRequestFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "certificateType",
        visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = X509CertificateRequestDetail.class, name = CertificateType.Codes.X509)})
@Schema(implementation = CertificateRequestDto.class)
public abstract class CertificateRequestDetail implements CertificateRequestDto {

    @Schema(description = "UUID of the Certificate Request", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID uuid;

    @Schema(description = "Certificate type", requiredMode = Schema.RequiredMode.REQUIRED)
    private CertificateType certificateType;

    @Schema(description = "Certificate request format", requiredMode = Schema.RequiredMode.REQUIRED)
    private CertificateRequestFormat certificateRequestFormat;

    @Schema(description = "Public key algorithm. Absent for certificate types without a public key",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String publicKeyAlgorithm;

    @Schema(description = "Certificate signature algorithm. Absent for certificate types whose requests are not signed",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String signatureAlgorithm;

    @Schema(description = "Certificate request content", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "Name to show for the certificate: the common name of an X.509 certificate, the "
            + "identifier of a certificate type without one", requiredMode = Schema.RequiredMode.REQUIRED)
    private String displayName;

    @Schema(description = "Certificate common name. Absent for certificate types without a common name",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String commonName;

    @Schema(description = "CSR Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> attributes;

    @Schema(description = "Signature Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> signatureAttributes;

    @Schema(description = "UUID of the Key", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String keyUuid;

    @Schema(description = "Certificate request compliance status", requiredMode = Schema.RequiredMode.REQUIRED)
    private ComplianceStatus complianceStatus;
}
