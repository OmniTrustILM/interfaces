package com.otilm.api.model.common.attribute.v3.mapping;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Declares how Core resolves the content of an attribute; orthogonal to fieldMapping")
public class ValueSource {

    @Schema(description = "How the attribute value is resolved", requiredMode = Schema.RequiredMode.REQUIRED)
    private ValueSourceType kind;

    @Schema(description = "Optional dependency filters that scope the source by other attributes' values")
    private List<SourceParam> params;
}
