package com.czertainly.api.model.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class-level constraint: if {@code allowedAuthenticationMethods} contains {@code BASIC_PASSWORD},
 * {@code basicCredentials} must be non-empty.
 */
@Constraint(validatedBy = BasicCredentialsRequiredIfBasicPasswordValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BasicCredentialsRequiredIfBasicPassword {

    String message() default "basicCredentials must contain at least one entry when BASIC_PASSWORD is an allowed authentication method";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
