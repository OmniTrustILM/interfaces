package com.otilm.api.model.client.cmp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = UuidListValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidUuidList {

    String message() default "Invalid UUID list. All UUIDs in the list must be valid UUID";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}