package com.otilm.api.model.core.search;

import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.enums.PlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class SearchFieldDataDto {
    @Schema(description = "Identifier of field to search",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String fieldIdentifier;

    @Schema(description = "Label for the field",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String fieldLabel;

    @Schema(description = "Type of the field",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private FilterFieldType type;

    @Schema(description = "List of available conditions for the field",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<FilterConditionOperator> conditions;

    @Schema(description = "Platform enum of the field values",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private PlatformEnum platformEnum;

    @Schema(description = "Attribute filter field content type",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private AttributeContentType attributeContentType;

    @Schema(description = "Available values for the field")
    private Object value;

    @Schema(description = "Multivalue flag. true = yes, false = no")
    private Boolean multiValue;

    public Boolean isMultiValue() {
        return multiValue;
    }
}
