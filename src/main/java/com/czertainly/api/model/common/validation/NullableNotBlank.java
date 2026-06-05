package com.czertainly.api.model.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = NullableNotBlankValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NullableNotBlank {

    String message() default "Name cannot be blank if provided";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
