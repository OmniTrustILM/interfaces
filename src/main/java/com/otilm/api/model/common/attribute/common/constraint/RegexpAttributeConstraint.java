package com.otilm.api.model.common.attribute.common.constraint;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Setter
@Schema(
        description = "RegExp attribute constraint to restrict string value by regular expression",
        type = "object")
public class RegexpAttributeConstraint extends BaseAttributeConstraint<String> {

    @Schema(description = "Regular Expression Attribute Constraint Data")
    private String data;

    public RegexpAttributeConstraint(String description, String errorMessage, String data) {
        super(description, errorMessage, AttributeConstraintType.REGEXP);
        this.data = data;
    }

    public RegexpAttributeConstraint() {
        super(AttributeConstraintType.REGEXP);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("data", data)
                .toString();
    }
}
