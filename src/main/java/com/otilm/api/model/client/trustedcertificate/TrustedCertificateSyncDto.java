package com.otilm.api.model.client.trustedcertificate;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Health of the comparison between the trusted certificates and the platform")
public class TrustedCertificateSyncDto {

    @Schema(description = "Whether the platform trust has been loaded since the upgrade",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean initialImportDone;

    @Schema(description = "When the platform was last compared successfully",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime lastSuccessfulCheckAt;

    @Schema(description = "Why the last comparison failed, absent when it succeeded",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String lastError;

}
