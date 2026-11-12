package com.otilm.api.model.core.certificate;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The request a certificate was issued from, which a certificate type extends with what only its requests have. The
 * subtype is selected by {@code certificateType}.
 */
@Schema(description = "Certificate request dependent on the certificate type", type = "object",
        discriminatorProperty = "certificateType",
        discriminatorMapping = {
                @DiscriminatorMapping(value = CertificateType.Codes.X509, schema = X509CertificateRequestDetail.class)},
        oneOf = {X509CertificateRequestDetail.class})
public interface CertificateRequestDto {

    @Schema(description = "Certificate type", requiredMode = Schema.RequiredMode.REQUIRED)
    CertificateType getCertificateType();
}
