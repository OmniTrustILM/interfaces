package com.otilm.api.model.connector.v3.certificate;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.connector.v3.AuthorityV3ScopedRequestDto;
import com.otilm.api.model.core.enums.CertificateRequestFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Body for v3 /renew. Cert identity is parsed from existingCertificate (serial + issuer DN).
 * Optional meta carries the connector's tracking handle from the original issue/renew/register
 * so stateless connectors (e.g. EJBCA) can resolve the upstream end-entity without an extra lookup.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class CertificateRenewRequestDtoV3 extends AuthorityV3ScopedRequestDto {

    @Schema(description = "Certificate signing request, Base64-encoded. Optional when reuseKey=true.",
            format = "byte",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String request;

    @Schema(description = "CSR format",
            defaultValue = "pkcs10",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CertificateRequestFormat format;

    @Schema(description = "Base64 of cert to renew. Serial + issuer DN parsed from this constitute the cert identity at the CA.",
            format = "byte",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "existingCertificate is required for renew")
    private String existingCertificate;

    @Schema(description = "When true, request (CSR) is optional. Proof-of-possession is delegated to the upstream CA's renewal policy.",
            defaultValue = "false",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private boolean reuseKey;

    @Schema(description = "Renew-specific dynamic attributes (schema from shared /issue/attributes endpoint)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> attributes;

    @Schema(description = "Optional connector-defined metadata returned by the original issue/renew/register response. "
                  + "Replayed here so a stateless connector can resolve the upstream end-entity without an extra lookup.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataAttribute> meta;

    @Schema(description = "Typed certificate identity content projected from request attributes. "
                  + "The CSR remains authoritative for the public key and proof of possession.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CertificateRequestContent requestContent;

    /**
     * Renew requires a CSR unless {@code reuseKey=true} (in which case proof-of-possession is
     * delegated to the upstream CA's renewal policy).
     */
    @AssertTrue(message = "request (CSR) is required for renew unless reuseKey=true")
    @JsonIgnore
    @Schema(hidden = true)
    public boolean isRequestProvidedOrKeyReused() {
        return reuseKey || (request != null && !request.isBlank());
    }
}
