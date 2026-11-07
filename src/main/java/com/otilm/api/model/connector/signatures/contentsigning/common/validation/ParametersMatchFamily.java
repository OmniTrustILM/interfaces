package com.otilm.api.model.connector.signatures.contentsigning.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A {@code computeDtbs} request carries signature parameters of its own family only. The violation lands on
 * {@code signatureParameters} for a client that maps violations to fields.
 */
@Constraint(validatedBy = ParametersMatchFamilyValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParametersMatchFamily {

    String message() default "signatureParameters must belong to the request's family";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
