package com.otilm.api.model.core.oid.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.otilm.api.model.core.oid.ExtensionValueEncoding;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "How a certificate extension's value is carried. The valueSchema field is named for the job "
        + "rather than the notation: a value's shape is described by an ASN.1 module, which is what X.697 calls a "
        + "schema when it uses ASN.1 as one for JSON.")
public class CertificateExtensionOidPropertiesDto implements AdditionalOidPropertiesDto {

    @Schema(description = "Whether this extension should be marked critical by default when placed in a certificate",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "defaultCritical is required")
    private Boolean defaultCritical;

    @Schema(description = "ASN.1 encoding used to encode the attribute string value into the extension DER value",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "valueEncoding is required")
    private ExtensionValueEncoding valueEncoding;

    @Schema(description = "ASN.1 module defining the extension's value type, in the subset of X.680 the platform "
            + "supports; only applicable when valueEncoding is DER. An extension with a module takes values "
            + "written as JSON naming its members, in the JSON Encoding Rules of X.697; an extension without one "
            + "takes its value as base64-encoded DER.",
            example = "Demo DEFINITIONS IMPLICIT TAGS ::= BEGIN\n\n" + "ServiceEntitlement ::= SEQUENCE {\n"
                    + "     serviceId   UTF8String (SIZE (5..32)),\n" + "     tier        INTEGER (1..3) }\n\n" + "END",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String valueSchema;

    @AssertTrue(message = "valueSchema is only applicable when valueEncoding is DER")
    @JsonIgnore
    @Schema(hidden = true)
    private boolean isValueSchemaApplicable() {
        return valueSchema == null || valueEncoding == ExtensionValueEncoding.DER;
    }
}
