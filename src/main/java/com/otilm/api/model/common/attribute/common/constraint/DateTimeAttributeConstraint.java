package com.otilm.api.model.common.attribute.common.constraint;

import com.otilm.api.model.common.attribute.common.constraint.data.DateTimeAttributeConstraintData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Setter
@Schema(
        description = "DateTime attribute constraint to specify boundaries for date value",
        type = "object")
public class DateTimeAttributeConstraint extends BaseAttributeConstraint<DateTimeAttributeConstraintData> {

    @Schema(description = "DateTime Range Attribute Constraint Data")
    private DateTimeAttributeConstraintData data;

    public DateTimeAttributeConstraint(String description, String errorMessage, DateTimeAttributeConstraintData data) {
        super(description, errorMessage, AttributeConstraintType.DATETIME);
        this.data = data;
    }

    public DateTimeAttributeConstraint() {
        super(AttributeConstraintType.DATETIME);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("data", data)
                .toString();
    }
}
