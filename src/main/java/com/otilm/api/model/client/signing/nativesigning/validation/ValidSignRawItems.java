package com.otilm.api.model.client.signing.nativesigning.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Checks that every item of a {@code signRaw} request fits its input mode: under {@code dataDigest}, each item is as
 * long as a digest of the request's {@code digestAlgorithm}.
 */
@Constraint(validatedBy = SignRawItemsValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidSignRawItems {

    String message() default "items must match the signature input mode";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
