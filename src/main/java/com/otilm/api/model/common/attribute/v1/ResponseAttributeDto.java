package com.otilm.api.model.common.attribute.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * This class contains set of properties to represent
 * an Attribute definition including its value for the
 * detail API responses
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseAttributeDto {

    /**
     * UUID of the Attribute
     **/
    @Schema(
            description = "UUID of the Attribute",
            example = "b11c9be1-b619-4ef5-be1b-a1cd9ef265b7"
    )
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String uuid;

    /**
     * Name of the Attribute, can be used as key for form field label text
     **/
    @Schema(
            description = "Name of the Attribute",
            examples = {"Attribute"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    /**
    * Label of the Attribute, Can be used to display the field name in the User Interface
    **/
   @Schema(
           description = "Label of the the Attribute",
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
     * Content of the Attribute
     **/
    @Schema(
            description = "Content of the Attribute"
    )
    private Object content;

    public ResponseAttributeDto() {
        super();
    }

    public ResponseAttributeDto(ResponseAttributeDto original) {
        this.uuid = original.uuid;
        this.name = original.name;
        this.label = original.label;
        this.type = original.type;
        this.content = original.content;
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

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("type", type)
                .append("content", content)
                .toString();
    }
}
