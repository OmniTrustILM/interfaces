package com.otilm.api.model.connector.v3.authority;

import com.otilm.api.model.core.certificate.CertificateType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Schema(description = "Certificate type an authority issues")
public class SupportedCertificateTypeDtoV3 {

    @Schema(description = "Type of the Certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "certificateType is required")
    private CertificateType certificateType;

    public static SupportedCertificateTypeDtoV3 of(CertificateType certificateType) {
        SupportedCertificateTypeDtoV3 supported = new SupportedCertificateTypeDtoV3();
        supported.setCertificateType(certificateType);
        return supported;
    }
}
