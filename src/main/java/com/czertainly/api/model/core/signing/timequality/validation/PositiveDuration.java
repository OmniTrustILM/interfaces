package com.czertainly.api.model.core.signing.timequality.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Constraint(validatedBy = PositiveDurationValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PositiveDuration {

    String message() default "Duration must be positive (greater than zero)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
