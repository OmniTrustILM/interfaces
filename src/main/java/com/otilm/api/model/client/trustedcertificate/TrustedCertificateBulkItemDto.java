package com.otilm.api.model.client.trustedcertificate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Result of one requested certificate in a bulk trusted certificate operation")
public class TrustedCertificateBulkItemDto {

    @Schema(description = "The requested certificate: its UUID, or the fingerprint of an uploaded file",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String reference;

    @Schema(description = "UUID of the resulting Trusted Certificate, when there is one",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String uuid;

    @Schema(description = "Whether the operation succeeded for this certificate",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean success;

    @Schema(description = "Why the operation failed for this certificate, or what it did",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String message;

}
