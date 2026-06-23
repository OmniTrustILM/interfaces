package com.otilm.api.model.connector.v3.certificate;

import com.otilm.api.model.core.oid.ExtensionValueEncoding;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "A requested X.509 extension; SAN is never duplicated here — it is carried in subjectAltNames only")
public class RequestedExtension {

    @Schema(description = "OID of the X.509 extension (dotted-decimal, RFC 5280)",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Extension OID is required")
    private String oid;

    @Schema(description = "Whether the extension is marked critical",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Extension criticality is required")
    private boolean critical;

    @Schema(description = "Encoding of the value string",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Extension encoding is required")
    private ExtensionValueEncoding encoding;

    @Schema(description = "Extension value; a string whose interpretation is declared by encoding",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Extension value is required")
    private String value;
}
