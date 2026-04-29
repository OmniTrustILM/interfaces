package com.otilm.api.model.common.attribute.common.constraint;

import com.otilm.api.model.client.attribute.BaseAttributeConstraintDto;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", defaultImpl = RegexpAttributeConstraint.class, visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RegexpAttributeConstraint.class, name = AttributeConstraintType.Codes.REGEXP),
        @JsonSubTypes.Type(value = RangeAttributeConstraint.class, name = AttributeConstraintType.Codes.RANGE),
        @JsonSubTypes.Type(value = DateTimeAttributeConstraint.class, name = AttributeConstraintType.Codes.DATETIME)
})
@Schema(implementation = BaseAttributeConstraintDto.class)
public class BaseAttributeConstraint<T extends Serializable> extends AttributeConstraint {
    @Schema(description = "Description of the constraint")
    private String description;

    @Schema(description = "Error message to be displayed for wrong data")
    private String errorMessage;

    @Schema(description = "Attribute Constraint Type", requiredMode = Schema.RequiredMode.REQUIRED)
    private AttributeConstraintType type;

    @Hidden
    @Schema(description = "Attribute Constraint Data", requiredMode = Schema.RequiredMode.REQUIRED)
    private T data;

    public BaseAttributeConstraint(String description, String errorMessage, AttributeConstraintType type) {
        this.description = description;
        this.errorMessage = errorMessage;
        this.type = type;
    }

    public BaseAttributeConstraint(AttributeConstraintType type) {
        this.type = type;
    }

    //Empty constructor needed for serialization/deserialization purpose
    public BaseAttributeConstraint() {
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("description", description)
                .append("errorMessage", errorMessage)
                .append("type", type)
                .append("data", data)
                .toString();
    }
}
