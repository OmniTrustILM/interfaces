package com.otilm.api.model.client.discovery.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A discovery window carries a positive lookback in the lookback mode and none in the other modes. The violation lands
 * on {@code lookback} for a client that maps violations to fields.
 */
@Constraint(validatedBy = ConsistentLookbackValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConsistentLookback {

    String message() default "must be a positive duration, and given only with the lookback mode";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
