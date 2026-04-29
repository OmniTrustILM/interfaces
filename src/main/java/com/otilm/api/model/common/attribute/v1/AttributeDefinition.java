package com.otilm.api.model.common.attribute.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * This class contains set of properties to represent
 * an Attribute definition including its value.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributeDefinition {

    /**
     * UUID of the Attribute
     **/
    @Schema(
            description = "UUID of the Attribute for unique identification",
            example = "b11c9be1-b619-4ef5-be1b-a1cd9ef265b7",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String uuid;

    /**
     * Name of the Attribute for processing
     **/
    @Schema(
            description = "Name of the Attribute that is used for identification",
            examples = {"Attribute"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    /**
     * Content of the Attribute
     **/
    @Schema(
            description = "Content of the Attribute"
    )
    private Object content;
    
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
     * Type of the Attribute, base types are defined in {@link AttributeType}
     **/
    @Schema(
            description = "Type of the Attribute",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private AttributeType type;

    /**
     * Boolean determining if the Attribute is required. If true, the Attribute must be provided.
     **/
    @Schema(
            description = "Boolean determining if the Attribute is required. If true, the Attribute must be provided.",
            defaultValue = "false",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean required = false;

    /**
     * Boolean determining if the Attribute is read only. If true, the Attribute content cannot be changed.
     **/
    @Schema(
            description = "Boolean determining if the Attribute is read only. If true, the Attribute content cannot be changed.",
            defaultValue = "false",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean readOnly = false;

    /**
     * Boolean determining if the Attribute is visible and can be displayed, otherwise it should be hidden to the user.
     **/
    @Schema(
            description = "Boolean determining if the Attribute is visible and can be displayed, otherwise it should be hidden to the user.",
            defaultValue = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean visible = true;

    /**
     * Boolean determining if the Attribute contains list of values in the content
     **/
    @Schema(
            description = "Boolean determining if the Attribute contains list of values in the content",
            defaultValue = "false",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean list = false;

    /**
     * Optional description of the Attribute, should contain helper text on what is expected
     **/
    @Schema(
            description = "Optional description of the Attribute, should contain helper text on what is expected"
    )
    private String description;

    /**
     * Optional regular expression used for validating the Attribute content
     **/
    @Schema(
            description = "Optional regular expression used for validating the Attribute content"
    )
    private String validationRegex;

    /**
     * Optional definition of callback for getting the content of the Attribute based on the action
     **/
    @Schema(
            description = "Optional definition of callback for getting the content of the Attribute based on the action"
    )
    private AttributeCallback attributeCallback;

    /**
     * Boolean determining if the Attribute can have multiple values
     **/
    @Schema(
            description = "Boolean determining if the Attribute can have multiple values",
            defaultValue = "false",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean multiSelect = false;

    /**
     * Group of the Attribute, used for the logical grouping of the Attribute
     **/
    @Schema(
            description = "Group of the Attribute, used for the logical grouping of the Attribute",
            examples = {"requiredAttributes"}
    )
    private String group;

    public AttributeDefinition() {
        super();
    }

    public AttributeDefinition(AttributeDefinition original) {
        this.uuid = original.uuid;
        this.name = original.name;
        this.content = original.content;
        this.label = original.label;
        this.type = original.type;
        this.required = original.required;
        this.readOnly = original.readOnly;
        this.visible = original.visible;
        this.list = original.list;
        this.description = original.description;
        this.validationRegex = original.validationRegex;
        this.attributeCallback = original.attributeCallback;
        this.multiSelect = original.multiSelect;
        this.group = original.group;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public AttributeType getType() {
        return type;
    }

    public void setType(AttributeType type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isList() {
        return list;
    }

    public void setList(boolean list) {
        this.list = list;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValidationRegex() {
        return validationRegex;
    }

    public void setValidationRegex(String validationRegex) {
        this.validationRegex = validationRegex;
    }

    public AttributeCallback getAttributeCallback() {
        return attributeCallback;
    }

    public void setAttributeCallback(AttributeCallback attributeCallback) {
        this.attributeCallback = attributeCallback;
    }

    public boolean isMultiSelect() {
        return multiSelect;
    }

    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
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
                .append("uuid", uuid)
                .append("name", name)
                .append("content", content)
                .append("type", type)
                .append("required", required)
                .append("readOnly", readOnly)
                .append("visible", visible)
                .append("description", description)
                .append("validationRegex", validationRegex)
                .append("attributeCallback", attributeCallback)
                .append("multiSelect", multiSelect)
                .append("group", group)
                .toString();
    }
}
