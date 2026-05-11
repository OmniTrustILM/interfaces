package com.czertainly.api.model.client.signing.timequality.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NtpCheckIntervalAccuracyValidator implements ConstraintValidator<ValidNtpCheckInterval, NtpIntervalAccuracyConfiguration> {

    @Override
    public boolean isValid(NtpIntervalAccuracyConfiguration value, ConstraintValidatorContext context) {
        if (value == null || value.getNtpCheckInterval() == null || value.getAccuracy() == null) {
            return true;
        }
        if (value.getNtpCheckInterval().compareTo(value.getAccuracy()) >= 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("ntpCheckInterval")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
