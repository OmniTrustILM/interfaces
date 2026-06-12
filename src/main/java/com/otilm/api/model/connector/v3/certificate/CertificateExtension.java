package com.otilm.api.model.connector.v3.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CertificateExtension {

    @Schema(description = "OID of the X.509 extension (dotted-decimal, RFC 5280)",
            example = "2.5.29.37",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Extension OID is required")
    private String oid;

    @Schema(description = "Whether the extension is marked critical",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean critical;

    @Schema(description = "DER-encoded extension value, Base64-encoded",
            format = "byte",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Extension valueBase64 is required")
    private String valueBase64;
}
