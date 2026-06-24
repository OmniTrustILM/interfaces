package com.otilm.api.model.common.attribute.v3.mapping;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Declares how Core resolves the content of an attribute; orthogonal to fieldMapping")
public class ValueSource implements Serializable {

    @Schema(description = "How the attribute value is resolved", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Value source kind is required")
    private ValueSourceType kind;

    @Schema(description = "Optional dependency filters that scope the source by other attributes' values")
    private List<SourceParam> params;
}
