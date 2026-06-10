package com.otilm.api.model.client.signing.profile.record.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SigningRecordPolicyValidator.class)
@Documented
public @interface ValidSigningRecordPolicy {
    String message() default "recordSignedDocument is only valid for CONTENT_SIGNING and TIMESTAMPING workflows";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
