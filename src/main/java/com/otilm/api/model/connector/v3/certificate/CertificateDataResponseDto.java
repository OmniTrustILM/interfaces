package com.otilm.api.model.connector.v3.certificate;

import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.core.certificate.CertificateType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Shared response envelope for v3 certificate operations.
 * Used as the body for:
 *   - issue/renew sync 200 (certificateData populated; meta optional)
 *   - issue/renew/register/revoke async 202 (certificateData null; meta populated)
 *   - register sync 200 (certificateData null — registration produces no cert; meta populated)
 *   - each entry in CaCertificatesResponseDto.certificates
 *
 * v2's `uuid` field is intentionally not present in v3 — stateless connectors own no
 * per-cert identity; Core assigns and stores certificate UUIDs in its own database.
 */
@Getter
@Setter
@ToString
public class CertificateDataResponseDto {

    @Schema(description = "Base64-encoded certificate content. Populated only for issue/renew sync 200. "
                  + "Null for any async 202 response; register sync 200 (registration produces no cert); "
                  + "revoke responses (revoke never carries a payload).",
            format = "byte",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String certificateData;

    @Schema(description = "Connector-defined metadata. Present on async 202 as the tracking handle Core "
                  + "replays on subsequent /status and /cancel calls; optional on 200 responses.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataAttribute> meta;

    @Schema(description = "Type hint for the certificate. Default X.509. Set explicitly when issuing non-X.509 "
                  + "certificates (e.g. SSH certificates) so Core does not need to infer type from certificateData.",
            defaultValue = "X.509",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CertificateType certificateType;
}
