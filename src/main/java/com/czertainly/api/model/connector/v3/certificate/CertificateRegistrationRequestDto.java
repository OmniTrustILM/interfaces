package com.czertainly.api.model.connector.v3.certificate;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.connector.v3.V3AuthorityScopedRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Body for v3 /register. Pre-registers a certificate's identity at the upstream CA
 * before a CSR exists. /issue later binds a CSR to this registration via registrationMeta.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class CertificateRegistrationRequestDto extends V3AuthorityScopedRequestDto {

    @Schema(description = "Subject DN in RFC 4514 string form",
            example = "CN=device-7,O=Acme",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String subjectDn;

    @Schema(description = "Subject Alternative Name in RFC 5280 textual form (e.g. DNS:foo,IP:1.2.3.4,email:x@y). "
                  + "SAN MUST be carried here ONLY — do not duplicate it as OID 2.5.29.17 in extensions[]. "
                  + "Connectors reject duplicate-source requests with VALIDATION_FAILED.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String subjectAltName;

    @Schema(description = "Structured certificate extensions (OID + critical + base64 value). "
                  + "All extensions OTHER than SAN go here.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<CertificateExtension> extensions;

    @Schema(description = "Register-specific dynamic attributes",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> attributes;
}
