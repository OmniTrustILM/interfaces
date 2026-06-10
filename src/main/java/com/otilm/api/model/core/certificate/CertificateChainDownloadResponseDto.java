package com.otilm.api.model.core.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CertificateChainDownloadResponseDto extends CertificateDownloadResponseDto {

    @Schema(
            description = "Indicator whether the chain returned is complete",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean completeChain;
}
