package com.otilm.api.model.core.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.core.enums.CertificateRequestFormat;
import com.otilm.api.model.core.logging.Sensitive;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Class representing a request to regenerate certificate
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClientCertificateRekeyRequestDto {

    @Schema(description = "True to replace renewed certificate in the associated locations", defaultValue = "false")
    public boolean replaceInLocations;

    // ------------------------------------------------------------------------------------------------------------------
    // Request Related Parameters
    // ------------------------------------------------------------------------------------------------------------------

    @Schema(description = "Certificate signing request encoded as Base64 string. If not provided, CSR attributes will be used")
    private String request;

    @Schema(description = "Certificate signing request format", defaultValue = "pkcs10")
    @Builder.Default
    private CertificateRequestFormat format = CertificateRequestFormat.PKCS10;

    // ------------------------------------------------------------------------------------------------------------------
    // Key Related Parameters
    // ------------------------------------------------------------------------------------------------------------------

    @Schema(description = "Key UUID")
    private UUID keyUuid;

    @Schema(description = "Token Profile UUID")
    private UUID tokenProfileUuid;

    @Schema(description = "Alternative Key UUID")
    private UUID altKeyUuid;

    @Schema(description = "Token Profile UUID for the alternative key")
    private UUID altTokenProfileUuid;

    // ------------------------------------------------------------------------------------------------------------------
    // Attributes
    // ------------------------------------------------------------------------------------------------------------------

    @Schema(description = "Signature Attributes. If not provided, existing attributes will be used to generate the new CSR")
    private List<RequestAttribute> signatureAttributes;

    @Schema(description = "Alternative Signature Attributes. If not provided, existing alternative attributes will be used to generate the new CSR")
    private List<RequestAttribute> altSignatureAttributes;

    @Schema(description = "Rekey dynamic attributes. Rekey is a renew at the authority — there is no separate rekey "
            + "operation on the connector contract — so this uses the renew schema, as listed by "
            + "GET /v2/operations/authorities/{authorityUuid}/raProfiles/{raProfileUuid}/attributes/renew. "
            + "Distinct from signatureAttributes/altSignatureAttributes, which configure CSR signing, not the CA operation.")
    private List<RequestAttribute> attributes;

    // Format (@Size/@Pattern) is enforced at registration, not here: on the verify path a malformed secret
    // must fail the challenge identically to a wrong one, so no format constraint is applied (no format oracle).
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "One-time authorization secret for rekeying a certificate that has an active "
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
                .append("keyUuid", keyUuid)
                .toString();
    }

    /**
     * Partial builder declaration; Lombok fills in the rest. Declared only to replace the generated builder
     * {@code toString()}, which would otherwise print every builder field — including the write-only
     * {@code authorizationSecret} and the CSR payload, which the DTO's own allowlisted {@code toString()} deliberately
     * omits.
     */
    public static class ClientCertificateRekeyRequestDtoBuilder {
        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                    .append("replaceInLocations", replaceInLocations)
                    .append("keyUuid", keyUuid)
                    .toString();
        }
    }
}
