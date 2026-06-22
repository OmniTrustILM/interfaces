package com.otilm.api.model.common.attribute.v3.mapping;

import com.otilm.api.model.core.certificate.GeneralNameType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Maps an attribute value to a Subject Alternative Name entry")
public class SanMappedField extends MappedField {

    @Schema(description = "SAN type", requiredMode = Schema.RequiredMode.REQUIRED)
    private GeneralNameType generalNameType;

    @Schema(description = "OID of the otherName type; required when generalNameType is OTHER_NAME")
    private String otherNameOid;
}
