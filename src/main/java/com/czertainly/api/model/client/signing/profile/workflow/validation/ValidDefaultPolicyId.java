package com.czertainly.api.model.client.signing.profile.workflow.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that {@code defaultPolicyId}, when set together with a non-empty {@code allowedPolicyIds},
 * is itself a member of the allowlist.
 */
@Constraint(validatedBy = DefaultPolicyIdValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidDefaultPolicyId {

    String message() default "Default policy ID must be among the allowed policy IDs";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
