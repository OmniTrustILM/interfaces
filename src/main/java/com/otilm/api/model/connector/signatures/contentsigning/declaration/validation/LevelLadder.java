package com.otilm.api.model.connector.signatures.contentsigning.declaration.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** The levels run up from SIGNED without a gap, so the last one is the family's ceiling. */
@Constraint(validatedBy = LevelLadderValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LevelLadder {

    String message() default "must run up from SIGNED without a gap";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
