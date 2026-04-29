package com.otilm.api.model.client.attribute.metadata;

import com.otilm.api.model.client.attribute.AttributeDefinitionDto;
import com.otilm.api.model.common.attribute.common.AttributeType;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
     * Friendly name of the Attribute
     **/
    @Schema(
            description = "Friendly name of the the Attribute",
            examples = {"Attribute Name"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String label;

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

    public AttributeType getType() {
        return type;
    }

    public void setType(AttributeType type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("type", type)
                .append("label", label)
                .append("visible", visible)
                .append("group", group)
                .toString();
    }
}
