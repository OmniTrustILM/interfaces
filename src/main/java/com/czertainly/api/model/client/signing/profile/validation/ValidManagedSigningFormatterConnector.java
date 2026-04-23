package com.czertainly.api.model.client.signing.profile.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class-level constraint that ensures {@code workflow.signatureFormatterConnectorUuid} is non-null
 * whenever the signing profile uses ILM-managed signing with a Timestamping or Content Signing workflow.
 */
@Constraint(validatedBy = ManagedSigningFormatterConnectorValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidManagedSigningFormatterConnector {
    String message() default "signatureFormatterConnectorUuid must be provided when using ILM-managed signing with a Timestamping or Content Signing workflow";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
