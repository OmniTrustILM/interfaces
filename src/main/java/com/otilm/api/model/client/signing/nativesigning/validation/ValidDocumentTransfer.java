package com.otilm.api.model.client.signing.nativesigning.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Checks that a {@code signDocument} request matches its transfer mode. Inline items carry only the document; a
 * {@code digestOnly} request names its {@code digestAlgorithm}, and each item carries only a digest of that size.
 */
@Constraint(validatedBy = DocumentTransferValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidDocumentTransfer {

    String message() default "items must match the transfer mode";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
