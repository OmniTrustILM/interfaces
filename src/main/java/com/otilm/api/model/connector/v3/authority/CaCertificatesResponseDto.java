package com.otilm.api.model.connector.v3.authority;

import com.otilm.api.model.connector.v3.certificate.CertificateDataResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@Schema(name = "CaCertificatesResponseDtoV3")
public class CaCertificatesResponseDto {

    @Schema(description = "CA certificate chain. Each entry reuses the issued-cert envelope so per-CA meta "
                  + "(chain position hints, AKI/SKI extracts) and certificateType (X509 default; SSH CAs override) "
                  + "are preserved. Convention: issuing CA first, root last.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<CertificateDataResponseDto> certificates;
}
