package com.otilm.api.model.client.attribute.metadata;

import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class GlobalMetadataCreateRequestDto {

    /**
     * Name of the Attribute
     */
    @Schema(description = "Name of the Attribute", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * Content Type of the Attribute
     */
    @Schema(description = "Attribute Content Type", requiredMode = Schema.RequiredMode.REQUIRED)
    private AttributeContentType contentType;

    /**
     * Description of the Attribute
     */
    @Schema(description = "Attribute description")
    private String description;

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
    private boolean visible = true;


    /**
     * Group of the Attribute, used for the logical grouping of the Attribute
     **/
    @Schema(
            description = "Group of the Attribute, used for the logical grouping of the Attribute",
            examples = {"requiredAttributes"}
    )
    private String group;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AttributeContentType getContentType() {
        return contentType;
    }

    public void setContentType(AttributeContentType contentType) {
        this.contentType = contentType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
                .append("name", name)
                .append("contentType", contentType)
                .append("description", description)
                .append("label", label)
                .append("visible", visible)
                .append("group", group)
                .toString();
    }
}
