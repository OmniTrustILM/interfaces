package com.otilm.api.model.core.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CertificateContentDto {

    @Schema(description = "UUID of the Certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    private String uuid;

    @Schema(description = "Name to show for the certificate: the common name of an X.509 certificate, the "
            + "identifier of a certificate type without one", requiredMode = Schema.RequiredMode.REQUIRED)
    private String displayName;

    @Schema(description = "Certificate common name. Absent for certificate types without a common name",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String commonName;

    @Schema(description = "Certificate serial number. Absent for certificate types without one",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String serialNumber;

    @Schema(description = "Base64 encoded Certificate content", requiredMode = Schema.RequiredMode.REQUIRED)
    private String certificateContent;
}
