package com.otilm.api.model.connector.cryptography.v2.lifecycle.validation;

import com.otilm.api.model.connector.cryptography.v2.lifecycle.CreateCertificateRequestV2Dto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OneSignerSourceValidator implements ConstraintValidator<OneSignerSource, CreateCertificateRequestV2Dto> {

    private static final String REPORTED_PROPERTY = "signer";

    @Override
    public boolean isValid(CreateCertificateRequestV2Dto value, ConstraintValidatorContext context) {
        if (value == null || hasExactlyOneSignerSource(value)) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context
                .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(REPORTED_PROPERTY)
                .addConstraintViolation();
        return false;
    }

    private static boolean hasExactlyOneSignerSource(CreateCertificateRequestV2Dto value) {
        return (value.getSigner() != null) ^ (value.getSignerAttributes() != null);
    }
}
