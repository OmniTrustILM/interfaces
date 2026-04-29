package com.otilm.api.model.client.cmp.validation;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = CmpProfileNameValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidCmpProfileName {

    String message() default "CMP Profile Name can contain only unreserved URI characters " +
            "(alphanumeric, hyphen, period, underscore, and tilde)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}