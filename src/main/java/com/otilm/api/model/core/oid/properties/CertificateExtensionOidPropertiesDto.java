package com.otilm.api.model.core.oid.properties;

import com.otilm.api.model.core.oid.ExtensionValueEncoding;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CertificateExtensionOidPropertiesDto implements AdditionalOidPropertiesDto {

    @Schema(description = "Whether this extension should be marked critical by default when placed in a certificate",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "defaultCritical is required")
    private Boolean defaultCritical;

    @Schema(description = "ASN.1 encoding used to encode the attribute string value into the extension DER value",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "valueEncoding is required")
    private ExtensionValueEncoding valueEncoding;
}
