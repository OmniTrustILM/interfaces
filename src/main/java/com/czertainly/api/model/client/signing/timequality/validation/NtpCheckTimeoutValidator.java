package com.czertainly.api.model.client.signing.timequality.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NtpCheckTimeoutValidator implements ConstraintValidator<ValidNtpCheckTimeout, NtpCheckIntervalConfiguration> {

    @Override
    public boolean isValid(NtpCheckIntervalConfiguration value, ConstraintValidatorContext context) {
        if (value == null || value.getNtpCheckTimeout() == null || value.getNtpCheckInterval() == null) {
            return true;
        }
        if (value.getNtpCheckTimeout().compareTo(value.getNtpCheckInterval()) >= 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("ntpCheckTimeout")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
