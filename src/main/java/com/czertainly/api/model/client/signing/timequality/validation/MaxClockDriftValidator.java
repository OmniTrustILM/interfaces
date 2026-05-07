package com.czertainly.api.model.client.signing.timequality.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MaxClockDriftValidator implements ConstraintValidator<ValidMaxClockDrift, ClockDriftConfiguration> {

    @Override
    public boolean isValid(ClockDriftConfiguration value, ConstraintValidatorContext context) {
        if (value == null || value.getMaxClockDrift() == null || value.getAccuracy() == null) {
            return true;
        }
        if (value.getMaxClockDrift().compareTo(value.getAccuracy()) >= 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("maxClockDrift")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
