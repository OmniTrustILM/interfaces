package com.otilm.api.model.connector.cryptography.v2.key.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class-level constraint: a key whose algorithm is sized by length states that length. It is reported on
 * {@code length}, so a client mapping violations to fields finds the field it has to fill.
 */
@Constraint(validatedBy = LengthWhenSizedByLengthValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LengthWhenSizedByLength {

    String message() default "key length is required for RSA, ECDSA, and AES";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
