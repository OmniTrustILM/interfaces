package com.otilm.api.model.common.attribute.v3.mapping;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Dependency filter that scopes a value source by another attribute's value")
@Data
public class SourceParam implements Serializable {

    @Schema(description = "UUID of the attribute whose value is used as a filter parameter")
    private String attributeUuid;

    @Schema(description = "Name of the attribute; used as fallback when attributeUuid is not matched")
    private String attributeName;
}
