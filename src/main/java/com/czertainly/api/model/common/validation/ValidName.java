package com.czertainly.api.model.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that a name contains only unreserved URI characters as defined in RFC 3986:
 * alphanumeric characters (a-z, A-Z, 0-9), hyphen (-), period (.), underscore (_), and tilde (~).
 *
 * <p>Null and empty string values are considered valid. Combine with {@code @NotBlank} to also reject null, empty, and blank values.
 */
@Constraint(validatedBy = ValidNameValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidName {

    String message() default "Name can contain only unreserved URI characters (alphanumeric, hyphen, period, underscore, and tilde)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
