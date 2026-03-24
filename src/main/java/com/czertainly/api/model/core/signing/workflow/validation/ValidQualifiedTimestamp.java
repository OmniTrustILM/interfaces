package com.czertainly.api.model.core.signing.workflow.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class-level constraint that enforces the rule:
 * if {@code qualifiedTimestamp} is {@code true}, then {@code timeQualityConfigurationUuid}
 * must be provided.
 *
 * <p>Rationale: ETSI EN 319 421 requires a qualified TSA to demonstrate clock accuracy,
 * which is captured by a {@link com.czertainly.api.model.core.signing.timequality.TimeQualityConfigurationDto}.
 * Without a time-quality configuration, the qualified-timestamp guarantee cannot be verified.</p>
 *
 * <p>Apply to any class that implements {@link QualifiedTimestampConstrained}.</p>
 */
@Constraint(validatedBy = QualifiedTimestampValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidQualifiedTimestamp {

    String message() default
            "timeQualityConfigurationUuid is required when qualifiedTimestamp is true (ETSI EN 319 421)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
