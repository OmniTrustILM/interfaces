package com.otilm.api.model.core.certificate;

import com.otilm.api.model.core.cryptography.key.KeyDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class X509CertificateTypeDetailsDto implements CertificateTypeDetailsDto {

    @Schema(description = "Common name of the certificate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String commonName;

    @Schema(description = "Subject DN of the certificate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String subjectDn;

    @Schema(description = "Issuer DN of the certificate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String issuerDn;

    @Schema(description = "Serial number of the issuer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String issuerSerialNumber;

    @Schema(description = "Certificate subject type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CertificateSubjectType subjectType;

    @Schema(description = "Key usages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<CertificateKeyUsage> keyUsage;

    @Schema(description = "Extended key usages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> extendedKeyUsage;

    @Schema(description = "Subject alternative names", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<String, List<String>> subjectAlternativeNames;

    @Schema(description = "Indicator whether the certificate is hybrid",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private boolean hybridCertificate;

    @Schema(description = "Alternative public key algorithm of a hybrid certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String altPublicKeyAlgorithm;

    @Schema(description = "Alternative signature algorithm of a hybrid certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String altSignatureAlgorithm;

    @Schema(description = "Key size of the alternative key of a hybrid certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer altKeySize;

    @Schema(description = "Alternative Key Pair of a hybrid certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private KeyDto altKey;

    @Schema(description = "Parsed ETSI QCStatements extension (RFC 3739 / ETSI EN 319 412-5). "
            + "Absent when the extension is not present in the certificate.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CertificateQcStatementsDto qcStatements;
}
