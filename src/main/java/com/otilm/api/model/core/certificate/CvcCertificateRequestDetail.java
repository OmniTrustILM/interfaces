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

    @Schema(description = "Authority reference of the request: the holder reference of the previous certificate "
            + "when the request asks for a successor of it. Absent in an initial request",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String authorityReference;
}
