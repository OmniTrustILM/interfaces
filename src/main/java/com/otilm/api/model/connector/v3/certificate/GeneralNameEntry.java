package com.otilm.api.model.connector.v3.certificate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.otilm.api.model.common.validation.NullableNotBlank;
import com.otilm.api.model.core.certificate.GeneralNameType;
import com.otilm.api.model.core.oid.ExtensionValueEncoding;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Schema(description = "A single Subject Alternative Name entry")
public class GeneralNameEntry {

    @Schema(description = "SAN type", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "SAN type is required")
    private GeneralNameType type;

    @Schema(description = "SAN value; interpretation depends on type (IP address, DNS name, email, URI, etc.)",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "SAN value is required")
    private String value;

    @Schema(description = "OID of the otherName type; required when type is OTHER_NAME",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @NullableNotBlank(message = "otherNameOid must not be blank when type is OTHER_NAME")
    private String otherNameOid;

    @Schema(description = "Encoding of the otherName value; required when type is OTHER_NAME",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ExtensionValueEncoding valueEncoding;

    @AssertTrue(message = "An otherNameOid and valueEncoding must be provided when type is OTHER_NAME")
    @JsonIgnore
    @Schema(hidden = true)
    public boolean isOtherNameValid() {
        if (type == GeneralNameType.OTHER_NAME) {
            return otherNameOid != null && !otherNameOid.isBlank() && valueEncoding != null;
        }
        return true;
    }
}
