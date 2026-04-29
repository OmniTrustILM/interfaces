package com.otilm.api.model.core.v2;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.core.authority.CertificateRevocationReason;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Class with parameter to revoke any certificate.
 */
@Data
public class ClientCertificateRevocationDto {

    @Schema(description = "Reason for revocation",
            defaultValue = "UNSPECIFIED")
    private CertificateRevocationReason reason;

    @Schema(description = "List of Attributes to revoke Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> attributes;

    @Schema(
            description = "Destroy Key upon successful revocation",
            defaultValue = "false"
    )
    private boolean destroyKey;
}
