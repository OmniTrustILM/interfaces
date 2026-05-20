package com.czertainly.api.model.connector.v3.certificate;

import com.czertainly.api.model.common.attribute.common.MetadataAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Response from the v3 status endpoints. Reused across all four async operations
 * (issue, renew, revoke, register). For revoke and register at COMPLETED,
 * certificateData is always null.
 */
@Getter
@Setter
@ToString
public class CertificateOperationStatusResponseDto {

    @Schema(description = "Operation status as known to the connector",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private CertificateOperationStatus status;

    @Schema(description = "Base64-encoded certificate content. Populated when status=COMPLETED for issue/renew. "
                  + "Always null for revoke and register status responses.",
            format = "byte",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String certificateData;

    @Schema(description = "Optional updated connector-defined metadata",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataAttribute> meta;

    @Schema(description = "Failure detail when status=FAILED — curated message text (no raw exception messages)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String reason;
}
