package com.otilm.api.model.client.notification.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FrequencyValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFrequency {

    String message() default "Frequency can only contain days and hours.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
