package com.otilm.api.model.core.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.connector.v3.certificate.CertificateExtension;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Request body for the {@code POST /certificates/register} operator endpoint.
 *
 * <p>Reserves a slot or identity at the CA before any CSR exists. The CA returns a
 * tracking handle (carried in the response metadata) that is later supplied to the
 * standard issue flow to complete certificate issuance.</p>
 *
 * <p>Only valid against v3 authorities that advertise the
 * {@code CERTIFICATE_REGISTRATION} feature flag.</p>
 */
@Data
@Schema(
        name = "ClientCertificateRegistrationRequest",
        description = "Pre-registration request for a v3 authority. Reserves a slot/identity at the CA "
                + "before any CSR exists; the returned tracking handle is later used to complete issuance."
)
public class ClientCertificateRegistrationDto {

    @Schema(
            description = "Subject DN. Optional per RFC 5280 §4.1.2.6: an empty subject is permitted "
                    + "when subject naming information is carried entirely in the Subject Alternative "
                    + "Name extension (in which case the subjectAltName extension MUST be marked "
                    + "critical at issuance). At least one of subjectDn, subjectAltName, or csrAttributes "
                    + "must provide subject identity — enforced by the cross-field {@code subjectIdentificationProvided} "
                    + "constraint.",
            example = "CN=device-7,O=Acme",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String subjectDn;

    @Schema(
            description = "Subject Alternative Name in RFC 5280 textual form (e.g. DNS:foo,IP:1.2.3.4,email:x@y). "
                    + "SAN MUST be carried here ONLY — do not also include it as OID 2.5.29.17 in extensions[]. "
                    + "Connectors reject duplicate-source requests with VALIDATION_FAILED. "
                    + "Provides subject identity when subjectDn is empty (see RFC 5280 §4.1.2.6); "
                    + "csrAttributes is a further alternative.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String subjectAltName;

    @Schema(
            description = "Structured certificate extensions (OID + critical + base64 value). "
                    + "All extensions OTHER than SAN go here.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @Valid
    private List<CertificateExtension> extensions;

    @Schema(
            description = "Structured request-attribute identity content (subject RDNs, SANs, extensions) — the "
                    + "typed alternative to the flat subjectDn/subjectAltName/extensions above, mirroring the issue "
                    + "path. When provided, the platform projects it into the registration identity; the flat "
                    + "subjectDn/subjectAltName/extensions are the simple alternative. Provide the identity via "
                    + "csrAttributes OR the flat fields, not both.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<RequestAttribute> csrAttributes;

    @Schema(
            description = "Authority-defined registration attributes (e.g., subject DN template, validity hints)."
    )
    private List<RequestAttribute> attributes;

    @Schema(
            description = "RA-profile-scoped override attributes for the registration."
    )
    private List<RequestAttribute> raProfileAttributes;

    @Schema(
            description = "List of Custom Attributes"
    )
    private List<RequestAttribute> customAttributes;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 12, max = 255)
    @Pattern(regexp = "[\\x20-\\x7E]+", message = "authorizationSecret must be printable ASCII")
    @Schema(
            description = "Authorization secret (challenge) that gates completion of this pre-registered "
                    + "certificate. Write-only and optional — the operator supplies it to opt the registration "
                    + "into challenge-gated issuance; the platform never generates one. Issuing a pre-registered "
                    + "certificate is currently the only challenge-verified completion path; renewal and rekey "
                    + "requests for a certificate with an active registration are rejected (fail-closed) until "
                    + "challenge-gated successor handling is added.",
            accessMode = Schema.AccessMode.WRITE_ONLY,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String authorizationSecret;

    @Future
    @Schema(
            description = "Optional absolute deadline by which the completion request must be presented — a valid "
                    + "challenge must be supplied before this instant. It gates the completion request, not the "
                    + "final issuance: when the request is approved or otherwise processed asynchronously, issuance "
                    + "may finalize shortly after the deadline. When omitted, the platform applies the default "
                    + "registration issuance window.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private OffsetDateTime expiresAt;

    /**
     * RFC 5280 §4.1.2.6: a certificate's subject identity may be carried in subjectDn, in the
     * subjectAltName extension, or both — but it cannot be empty in both. Structured identity may
     * instead arrive via csrAttributes (projected connector-side into a subject/SAN). Connector-side
     * issuance will require subjectAltName to be marked critical when subjectDn is empty; the operator
     * input is checked here only for the basic "at least one identity source is set" invariant.
     */
    @JsonIgnore
    @AssertTrue(message = "At least one of subjectDn, subjectAltName, or csrAttributes must provide subject identity (RFC 5280 §4.1.2.6)")
    @Schema(hidden = true)
    public boolean isSubjectIdentificationProvided() {
        return (subjectDn != null && !subjectDn.isBlank())
                || (subjectAltName != null && !subjectAltName.isBlank())
                || (csrAttributes != null && csrAttributes.stream().anyMatch(Objects::nonNull));
    }

    /**
     * The pre-registration identity comes either from structured csrAttributes or from the flat
     * subjectDn/subjectAltName/extensions fields — never both. Mixing the two forms is ambiguous
     * (which one defines the placeholder subject?), so it is rejected at the contract boundary
     * rather than surfacing as a less specific error from register handling.
     */
    @JsonIgnore
    @AssertTrue(message = "Provide the pre-registration identity via csrAttributes or the flat subjectDn/subjectAltName/extensions fields, not both")
    @Schema(hidden = true)
    public boolean isSingleIdentitySource() {
        boolean structured = csrAttributes != null && csrAttributes.stream().anyMatch(Objects::nonNull);
        boolean flat = (subjectDn != null && !subjectDn.isBlank())
                || (subjectAltName != null && !subjectAltName.isBlank())
                || (extensions != null && !extensions.isEmpty());
        return !(structured && flat);
    }
}
