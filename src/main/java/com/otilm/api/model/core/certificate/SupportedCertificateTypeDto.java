package com.otilm.api.model.core.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Schema(description = "Certificate type that can be issued through an RA profile")
public class SupportedCertificateTypeDto {

    @Schema(description = "Type of the Certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    private CertificateType certificateType;

    public static SupportedCertificateTypeDto of(CertificateType certificateType) {
        SupportedCertificateTypeDto supported = new SupportedCertificateTypeDto();
        supported.setCertificateType(certificateType);
        return supported;
    }
}
