package com.czertainly.api.model.core.signing.signatureprofile.workflow.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = OidListValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidOidList {

    String message() default "Invalid OID list. All OIDs in the list must be in valid OID format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
