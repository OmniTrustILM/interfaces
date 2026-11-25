package com.otilm.api.model.connector.discovery.v2.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class-level constraint holding a discovered key's {@code algorithmIdentifier} to canonical DER and to the keys it may
 * describe. Each violation names the broken rule alone, for grouping skipped items by rule.
 */
@Constraint(validatedBy = AlgorithmIdentifierFitsKeyValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AlgorithmIdentifierFitsKey {

    String message() default "algorithmIdentifier must fit the discovered key";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
