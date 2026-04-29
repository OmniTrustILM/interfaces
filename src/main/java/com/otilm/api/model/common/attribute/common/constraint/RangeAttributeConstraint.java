package com.otilm.api.model.common.attribute.common.constraint;

import com.otilm.api.model.common.attribute.common.constraint.data.RangeAttributeConstraintData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Setter
@Schema(
        description = "Range attribute constraint to specify boundaries for integer value",
        type = "object")
public class RangeAttributeConstraint extends BaseAttributeConstraint<RangeAttributeConstraintData> {

    @Schema(description = "Integer Range Attribute Constraint Data")
    private RangeAttributeConstraintData data;

    public RangeAttributeConstraint(String description, String errorMessage, RangeAttributeConstraintData data) {
        super(description, errorMessage, AttributeConstraintType.RANGE);
        this.data = data;
    }

    public RangeAttributeConstraint() {
        super(AttributeConstraintType.RANGE);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("data", data)
                .toString();
    }
}
