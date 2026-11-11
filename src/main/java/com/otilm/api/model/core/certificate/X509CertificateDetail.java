package com.otilm.api.model.core.certificate;

import com.otilm.api.model.core.cryptography.key.KeyDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class X509CertificateDetail extends CertificateDetail {

    @Schema(description = "Extended key usages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> extendedKeyUsage;

    @Schema(description = "Key usages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<CertificateKeyUsage> keyUsage;

    @Schema(description = "Certificate subject type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CertificateSubjectType subjectType;

    @Schema(description = "Subject alternative names", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<String, List<String>> subjectAlternativeNames;

    @Schema(description = "Parsed ETSI QCStatements extension (RFC 3739 / ETSI EN 319 412-5). "
            + "Absent when the extension is not present in the certificate.")
    private CertificateQcStatementsDto qcStatements;

    @Schema(description = "Alternative Key Pair of the certificate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private KeyDto altKey;
}
