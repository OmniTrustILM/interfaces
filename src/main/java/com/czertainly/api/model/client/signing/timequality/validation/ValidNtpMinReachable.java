package com.czertainly.api.model.client.signing.timequality.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NtpMinReachableValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidNtpMinReachable {
    String message() default "ntpServersMinReachable must not exceed the number of configured ntpServers";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
