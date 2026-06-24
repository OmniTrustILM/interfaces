package com.otilm.api.model.common.attribute.v3.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.otilm.api.model.core.certificate.GeneralNameType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Maps an attribute value to a Subject Alternative Name entry")
public class SanMappedField extends MappedField {

    @Schema(description = "SAN type", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "SAN type is required")
    private GeneralNameType generalNameType;

    @Schema(description = "OID of the otherName type; required when generalNameType is OTHER_NAME")
    private String otherNameOid;

    @AssertTrue(message = "OtherName OID is required when SAN type is OTHER_NAME")
    @JsonIgnore
    @Schema(hidden = true)
    private boolean isOtherNameOidValid() {
        return generalNameType != GeneralNameType.OTHER_NAME || (otherNameOid != null && !otherNameOid.isEmpty());
    }
}
