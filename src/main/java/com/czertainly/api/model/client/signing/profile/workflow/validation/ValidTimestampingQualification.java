package com.czertainly.api.model.client.signing.profile.workflow.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class-level constraint that ensures {@code timeQualityConfigurationUuid} is non-null
 * whenever {@code qualifiedTimestamp} is {@code true}.
 */
@Constraint(validatedBy = TimestampingQualificationValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidTimestampingQualification {
    String message() default "timeQualityConfigurationUuid must be provided when qualifiedTimestamp is true";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
