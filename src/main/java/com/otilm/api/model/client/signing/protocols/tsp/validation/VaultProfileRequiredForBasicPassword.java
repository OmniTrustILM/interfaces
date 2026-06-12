package com.otilm.api.model.client.signing.protocols.tsp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class-level constraint: if {@code allowedAuthenticationMethods} contains {@code BASIC_PASSWORD},
 * {@code vaultProfileUuid} must be set.
 */
@Constraint(validatedBy = VaultProfileRequiredForBasicPasswordValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VaultProfileRequiredForBasicPassword {

    String message() default "when BASIC_PASSWORD is an allowed authentication method, vaultProfileUuid must be set";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
