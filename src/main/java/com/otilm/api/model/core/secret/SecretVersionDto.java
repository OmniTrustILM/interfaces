package com.otilm.api.model.core.secret;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class SecretVersionDto {

    @Schema(description = "Version number of the secret", requiredMode = Schema.RequiredMode.REQUIRED)
    private int version;

    @Schema(description = "Date and time when the secret version was created", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime createdAt;

    @Schema(description = "Fingerprint of the secret version, used for integrity verification", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fingerprint;
}
