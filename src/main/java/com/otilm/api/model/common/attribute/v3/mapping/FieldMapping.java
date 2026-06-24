package com.otilm.api.model.common.attribute.v3.mapping;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Declares which certificate (or other object) fields an attribute value projects into")
public class FieldMapping implements Serializable {

    @Schema(description = "Object type this mapping applies to", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Object type is required")
    private ObjectType objectType;

    @Schema(description = "One or more target fields (supports 1-to-many, e.g. CN + dNSName from a single attribute)",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Mapped Fields must be non-empty")
    private List<MappedField> fields;
}
