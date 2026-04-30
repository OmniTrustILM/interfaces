package com.czertainly.api.model.client.signing.timequality.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = HostnameListValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidHostnameList {
    String message() default "Invalid hostname list. All entries must be valid RFC 1123 hostnames or IP addresses (IPv4/IPv6)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
