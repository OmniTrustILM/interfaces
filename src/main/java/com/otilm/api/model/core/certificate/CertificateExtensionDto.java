package com.otilm.api.model.core.certificate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CertificateExtensionDto",
        description = "X.509 extension with its DER value and, when available, its decoded JER form")
public class CertificateExtensionDto {

    @Schema(description = "Extension object identifier in dotted notation", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "2.5.29.15")
    private String oid;

    @Schema(description = "Whether the extension is marked critical", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean critical;

    @Schema(description = "Base64 extension value (the contents of the extnValue OCTET STRING, "
            + "the same form RequestedExtension.value uses with DER encoding)",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String der;

    @Schema(description = "Extension value decoded to JSON Encoding Rules (JER). Present only when the OID has "
            + "a registered ASN.1 module and decoding succeeded.")
    private JsonNode jer;
}
