package com.czertainly.api.model.client.attribute.metadata;

import com.czertainly.api.model.client.attribute.AttributeDefinitionDto;
import com.czertainly.api.model.common.attribute.common.AttributeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Setter
@Getter
public class GlobalMetadataDefinitionDetailDto extends AttributeDefinitionDto {

    /**
     * Type of the Attribute. For the custom attribute, the type will always be "custom"
     */
    @Schema(description = "Type of the Attribute",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"custom"},
            defaultValue = "custom")
    private AttributeType type;

    /**
     * Boolean determining if the Attribute is visible and can be displayed, otherwise it should be hidden to the user.
     **/
    @Schema(
            description = "Boolean determining if the Attribute is visible and can be displayed, otherwise it should be hidden to the user.",
            defaultValue = "true"
    )
    private boolean visible;


    /**
     * Group of the Attribute, used for the logical grouping of the Attribute
     **/
    @Schema(
            description = "Group of the Attribute, used for the logical grouping of the Attribute",
            examples = {"requiredAttributes"}
    )
    private String group;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("type", type)
                .append("label", getLabel())
                .append("visible", visible)
                .append("group", group)
                .toString();
    }
}
