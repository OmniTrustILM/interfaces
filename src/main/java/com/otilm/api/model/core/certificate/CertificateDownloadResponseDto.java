package com.otilm.api.model.core.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class CertificateDownloadResponseDto {

    @Schema(
            description = "Format of the downloaded content",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private CertificateFormat format;

    @Schema(
            description = "Encoding of the downloaded content",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private CertificateFormatEncoding encoding;

    @Schema(
            description = "Base64 encoded content in the specified format and encoding",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String content;

}
