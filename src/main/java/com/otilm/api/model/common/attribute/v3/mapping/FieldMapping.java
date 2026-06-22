package com.otilm.api.model.common.attribute.v3.mapping;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Declares which certificate (or other object) fields an attribute value projects into")
public class FieldMapping implements Serializable {

    @Schema(description = "Object type this mapping applies to", requiredMode = Schema.RequiredMode.REQUIRED)
    private ObjectType objectType;

    @Schema(description = "One or more target fields (supports 1-to-many, e.g. CN + dNSName from a single attribute)",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<MappedField> fields;
}
