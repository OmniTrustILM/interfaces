package com.otilm.api.model.core.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class CertificateChainResponseDto {


    @Schema(
            description = "Indicator whether the chain returned is complete",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean completeChain;


    @Schema(
            description = "List of certificates in the chain",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<CertificateDetailDto> certificates;

}
