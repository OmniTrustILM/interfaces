package com.otilm.api.model.connector.authority;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CertificateRevocationListResponseDto {

    @Schema(description = "Base64 encoded CRL data",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private byte[] crlData;

}
