package com.otilm.api.model.core.certificate;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The detail of a certificate, which a certificate type extends with what only it has. The subtype is selected by
 * {@code certificateType}.
 */
@Schema(description = "Certificate detail dependent on the certificate type", type = "object",
        discriminatorProperty = "certificateType",
        discriminatorMapping = {
                @DiscriminatorMapping(value = CertificateType.Codes.X509, schema = X509CertificateDetail.class)},
        oneOf = {X509CertificateDetail.class})
public interface CertificateDetailDto {

    @Schema(description = "Certificate type", requiredMode = Schema.RequiredMode.REQUIRED)
    CertificateType getCertificateType();
}
