package com.otilm.api.model.client.trustedcertificate;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Result of a bulk trusted certificate operation. Item failures never change the status code")
public class TrustedCertificateBulkResultDto {

    @Schema(description = "One result per distinct requested certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<TrustedCertificateBulkItemDto> items;

}
