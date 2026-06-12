package com.otilm.api.model.connector.v3.certificate;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.connector.v3.AuthorityV3ScopedRequestDto;
import com.otilm.api.model.core.authority.CertificateRevocationReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Body for v3 /revoke. Cert identity is parsed from `certificate` (serial + issuer DN);
 * no separate identifier is needed for revocation. Optional meta supports CAs that
 * track revocation by an upstream handle (e.g. DigiCert order ID).
 */
@Getter
@Setter
@ToString(callSuper = true)
public class CertificateRevocationRequestDtoV3 extends AuthorityV3ScopedRequestDto {

    @Schema(description = "Base64 of cert to revoke. Serial + issuer DN parsed from this constitute the cert identity at the CA.",
            format = "byte",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "certificate is required for revoke")
    private String certificate;

    @Schema(description = "Revocation reason",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Revocation reason is required")
    private CertificateRevocationReason reason;

    @Schema(description = "Revoke-specific dynamic attributes",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> attributes;

    @Schema(description = "Optional connector-defined metadata. Populated when Core has the original issue meta. "
                  + "CAs that revoke purely by serial+issuer ignore it; CAs that track revocation via an upstream "
                  + "handle (e.g. DigiCert order ID) consume it.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataAttribute> meta;
}
