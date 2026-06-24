package com.otilm.api.model.common.attribute.v3.mapping;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Maps an attribute value to an X.509 extension")
public class ExtensionMappedField extends MappedField {

    @Schema(description = "OID of the X.509 extension (dotted-decimal)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String extensionOid;

    @Schema(description = "Whether the requester may override the default criticality of the extension")
    private boolean criticalOverridable;
}
