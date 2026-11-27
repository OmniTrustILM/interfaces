package com.otilm.api.model.core.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CvcCertificateRequestDetail extends CertificateRequestDetail {

    @Schema(description = "Holder reference the certificate is requested for",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String holderReference;

    @Schema(description = "Authority reference inside the request: the key of the certification authority that "
            + "the applicant expects to sign the certificate. The standard only recommends it, so it can be absent "
            + "in any request", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String authorityReference;

    @Schema(description = "Authority reference of the authentication around the request: the key that made the "
            + "outer signature. It is the applicant's previous certificate when the request asks for a successor, "
            + "or another trusted entity when it authenticates an initial request. Absent in a request that is not "
            + "authenticated", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String authenticatedBy;

    @Override
    @Schema(description = "Certificate type", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = CertificateType.Codes.CVC)
    public CertificateType getCertificateType() {
        return super.getCertificateType();
    }
}
