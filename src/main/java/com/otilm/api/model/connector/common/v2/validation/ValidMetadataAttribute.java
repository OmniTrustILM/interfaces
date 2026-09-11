package com.otilm.api.model.connector.common.v2.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Requires a metadata attribute to be usable as a handle.
 */
@Constraint(validatedBy = MetadataAttributeValidator.class)
@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidMetadataAttribute {

    String message() default "metadata attribute is not a usable handle";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
