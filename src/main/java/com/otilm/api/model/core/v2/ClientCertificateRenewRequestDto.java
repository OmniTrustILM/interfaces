package com.otilm.api.model.core.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.core.enums.CertificateRequestFormat;
import com.otilm.api.model.core.logging.Sensitive;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Class representing a request to renew certificate from external clients
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClientCertificateRenewRequestDto {

    @Schema(description = "True to replace renewed certificate in the associated locations", defaultValue = "false")
    private boolean replaceInLocations;

    @Schema(description = "Certificate signing request encoded as Base64 string. If not provided, Existing CSR will be used")
    private String request;

    @Schema(description = "Certificate signing request format", defaultValue = "pkcs10")
    @Builder.Default
    private CertificateRequestFormat format = CertificateRequestFormat.PKCS10;

    @Schema(description = "Renew-specific dynamic attributes, as listed by "
            + "GET /v2/operations/authorities/{authorityUuid}/raProfiles/{raProfileUuid}/attributes/renew. "
            + "Optional — an authority that offers no renew schema needs none.")
    private List<RequestAttribute> attributes;

    // Format (@Size/@Pattern) is enforced at registration, not here: on the verify path a malformed secret
    // must fail the challenge identically to a wrong one, so no format constraint is applied (no format oracle).
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "One-time authorization secret for renewing a certificate that has an active "
            + "registration. Write-only; ignored for certificates without one.",
            accessMode = Schema.AccessMode.WRITE_ONLY)
    @Sensitive
    private String authorizationSecret;

    // Deliberate allowlist — avoid leaking CSR/request or secrets via logs.
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("replaceInLocations", replaceInLocations)
                .append("format", format)
                .toString();
    }

    /**
     * Partial builder declaration; Lombok fills in the rest. Declared only to replace the generated builder
     * {@code toString()}, which would otherwise print every builder field — including the write-only
     * {@code authorizationSecret} and the CSR payload, which the DTO's own allowlisted {@code toString()} deliberately
     * omits.
     */
    public static class ClientCertificateRenewRequestDtoBuilder {
        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                    .append("replaceInLocations", replaceInLocations)
                    .toString();
        }
    }
}
