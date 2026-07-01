package com.otilm.api.model.connector.v3.certificate;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.connector.v3.AuthorityV3ScopedRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Body for v3 /register. Pre-registers a certificate's identity at the upstream CA
 * before a CSR exists. /issue later binds a CSR to this registration via meta.
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CertificateRegistrationRequestDtoV3 extends AuthorityV3ScopedRequestDto {

    @Schema(description = "Subject DN. Optional per RFC 5280 §4.1.2.6: an empty subject is permitted "
                  + "when subject naming information is carried entirely in the Subject Alternative "
                  + "Name extension (which the connector MUST then mark critical at issuance). "
                  + "At least one of subjectDn or subjectAltName must be non-empty.",
            example = "CN=device-7,O=Acme",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String subjectDn;

    @Schema(description = "Subject Alternative Name in OpenSSL convention textual form (e.g. DNS:foo,IP:1.2.3.4,email:x@y). "
                  + "SAN MUST be carried here ONLY — do not duplicate it as OID 2.5.29.17 in extensions[]. "
                  + "Connectors reject duplicate-source requests with VALIDATION_FAILED. "
                  + "Required when subjectDn is empty (see RFC 5280 §4.1.2.6).",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String subjectAltName;

    @Schema(description = "Structured certificate extensions (OID + critical + base64 value). "
                  + "All extensions OTHER than SAN go here.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Valid
    private List<CertificateExtension> extensions;

    @Schema(description = "Optional structured request content (typed RDNs, SANs, extensions). "
                  + "Present ONLY when the connector advertises the CERTIFICATE_REQUEST_STRUCTURED feature flag; "
                  + "otherwise Core renders the flat subjectDn/subjectAltName/extensions fields from the same content. "
                  + "The structured form is authoritative when both are present.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Valid
    private CertificateRequestContent requestContent;

    @Schema(description = "Register-specific dynamic attributes",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> attributes;

    /**
     * RFC 5280 §4.1.2.6: subject identity may be carried in subjectDn, in subjectAltName, or both
     * — but cannot be empty in both.
     *
     * <p>{@code requestContent} is intentionally not consulted here: it is supplementary. Core always renders the flat
     * {@code subjectDn} from the structured content. The flat fields therefore remain the single validation anchor.
     */
    @AssertTrue(message = "At least one of subjectDn or subjectAltName must be provided (RFC 5280 §4.1.2.6)")
    @JsonIgnore
    @Schema(hidden = true)
    public boolean isSubjectIdentificationProvided() {
        return (subjectDn != null && !subjectDn.isBlank())
                || (subjectAltName != null && !subjectAltName.isBlank());
    }
}
