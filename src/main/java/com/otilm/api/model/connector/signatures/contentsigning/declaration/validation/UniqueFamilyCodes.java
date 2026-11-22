package com.otilm.api.model.connector.signatures.contentsigning.declaration.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** A profile names its family by code alone, so two declarations of one code would leave its facts ambiguous. */
@Constraint(validatedBy = UniqueFamilyCodesValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UniqueFamilyCodes {

    String message() default "must not repeat a family code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
