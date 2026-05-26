package com.czertainly.api.model.core.v2;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.connector.v3.certificate.CertificateExtension;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

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
            description = "Subject DN in RFC 4514 string form. Required — registration creates a "
                    + "named identity at the upstream CA before a CSR exists.",
            example = "CN=device-7,O=Acme",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String subjectDn;

    @Schema(
            description = "Subject Alternative Name in RFC 5280 textual form (e.g. DNS:foo,IP:1.2.3.4,email:x@y). "
                    + "SAN MUST be carried here ONLY — do not also include it as OID 2.5.29.17 in extensions[]. "
                    + "Connectors reject duplicate-source requests with VALIDATION_FAILED.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String subjectAltName;

    @Schema(
            description = "Structured certificate extensions (OID + critical + base64 value). "
                    + "All extensions OTHER than SAN go here.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<CertificateExtension> extensions;

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

}
