package com.otilm.api.model.connector.v3.certificate;

import com.otilm.api.model.core.certificate.GeneralNameType;
import com.otilm.api.model.core.oid.ExtensionValueEncoding;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "A single Subject Alternative Name entry")
public class GeneralNameEntry {

    @Schema(description = "SAN type", requiredMode = Schema.RequiredMode.REQUIRED)
    private GeneralNameType type;

    @Schema(description = "SAN value; interpretation depends on type (IP address, DNS name, email, URI, etc.)",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String value;

    @Schema(description = "OID of the otherName type; required when type is OTHER_NAME",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String otherNameOid;

    @Schema(description = "Encoding of the otherName value; required when type is OTHER_NAME",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ExtensionValueEncoding valueEncoding;
}
