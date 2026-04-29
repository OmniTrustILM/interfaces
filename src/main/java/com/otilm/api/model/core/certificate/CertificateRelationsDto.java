package com.otilm.api.model.core.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CertificateRelationsDto {

    @Schema(
            description = "UUID of the certificate that is the subject of the request. " +
                    "Listed certificates include its source and descending certificates.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID certificateUuid;

    @Schema(
            description = "List of certificates that renew, rekey or replace the subject certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<CertificateSimpleDto> successorCertificates;

    @Schema(
            description = "List of certificates which the subject certificate renews, rekeys or replaces.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<CertificateSimpleDto> predecessorCertificates;
}
