package com.czertainly.api.model.core.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FingerprintDto {

    @Schema(description = "Fingerprint of the certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fingerprint;

}
