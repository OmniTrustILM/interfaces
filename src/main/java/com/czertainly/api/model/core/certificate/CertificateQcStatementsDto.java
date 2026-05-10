package com.czertainly.api.model.core.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class CertificateQcStatementsDto {

    @Schema(description = "Certificate is a qualified certificate per ETSI EN 319 412 (id-etsi-qcs-QcCompliance, OID 0.4.0.1862.1.1)")
    private Boolean qcCompliance;

    @Schema(description = "Private key resides in a Qualified Electronic Signature Creation Device "
            + "(id-etsi-qcs-QcSSCD, OID 0.4.0.1862.1.4)")
    private Boolean qcSscd;

    @Schema(description = "Intended purpose of the qualified certificate (id-etsi-qcs-QcType, OID 0.4.0.1862.1.6). "
            + "Absent means purpose is unspecified (all uses allowed by key usage).")
    private List<QcType> qcType;

    @Schema(description = "Country codes of national legislation under which the certificate is issued "
            + "(id-etsi-qcs-QcCClegislation, OID 0.4.0.1862.1.7). ISO 3166-1 alpha-2.")
    private List<String> qcCcLegislation;
}
