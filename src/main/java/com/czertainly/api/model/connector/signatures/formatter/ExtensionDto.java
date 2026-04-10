package com.czertainly.api.model.connector.signatures.formatter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "ExtensionDto", description = "ASN.1 extension (OID, criticality, value)")
public class ExtensionDto {

    @Schema(
            description = "OID of the extension",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "1.3.6.1.5.5.7.1.1"
    )
    private String oid;

    @Schema(
            description = "Whether the extension is critical",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean critical;

    @Schema(
            description = "Base64-encoded DER value of the extension",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String value;
}
