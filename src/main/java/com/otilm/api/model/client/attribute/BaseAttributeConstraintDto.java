package com.otilm.api.model.client.attribute;

import com.otilm.api.model.common.attribute.common.constraint.AttributeConstraintType;
import com.otilm.api.model.common.attribute.common.constraint.DateTimeAttributeConstraint;
import com.otilm.api.model.common.attribute.common.constraint.RangeAttributeConstraint;
import com.otilm.api.model.common.attribute.common.constraint.RegexpAttributeConstraint;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "BaseAttributeConstraint",
        description = "Base Attribute Constraint definition",
        type = "object",
        discriminatorProperty = "type",
        discriminatorMapping = {
                @DiscriminatorMapping(value = AttributeConstraintType.Codes.REGEXP, schema = RegexpAttributeConstraint.class),
                @DiscriminatorMapping(value = AttributeConstraintType.Codes.RANGE, schema = RangeAttributeConstraint.class),
                @DiscriminatorMapping(value = AttributeConstraintType.Codes.DATETIME, schema = DateTimeAttributeConstraint.class)
        },
        oneOf = {
                RegexpAttributeConstraint.class,
                RangeAttributeConstraint.class,
                DateTimeAttributeConstraint.class
        }
)
public interface BaseAttributeConstraintDto {
    @Schema(description = "Description of the constraint")
    String getDescription();

    @Schema(description = "Error message to be displayed for wrong data")
    String getErrorMessage();

    @Schema(description = "Attribute Constraint Type", requiredMode = Schema.RequiredMode.REQUIRED)
    AttributeConstraintType getType();


}
