package com.otilm.api.model.common.attribute.common.constraint.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

@Setter
@Getter
public class RangeAttributeConstraintData implements Serializable {
    @Schema(description = "Start of the range for validation")
    private Integer from;

    @Schema(description = "End of the range for validation")
    private Integer to;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("from", from)
                .append("to", to)
                .toString();
    }
}
