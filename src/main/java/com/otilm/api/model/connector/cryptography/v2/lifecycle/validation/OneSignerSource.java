package com.otilm.api.model.connector.cryptography.v2.lifecycle.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A certificate request names its signer once: as the bound signer or as the signer attributes of an unbound key. The
 * violation lands on {@code signer} for a client that maps violations to fields.
 */
@Constraint(validatedBy = OneSignerSourceValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OneSignerSource {

    String message() default "exactly one of signer and signerAttributes is required";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
