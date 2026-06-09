package com.otilm.api.model.connector.authority;

import com.otilm.api.model.connector.v2.CertificateDataResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CaCertificatesResponseDto {

    @Schema(
            description = "List of Certificates",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<CertificateDataResponseDto> certificates;

}
