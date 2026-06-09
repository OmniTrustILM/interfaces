package com.otilm.api.model.client.notification.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;

public class FrequencyValidator implements ConstraintValidator<ValidFrequency, Duration> {
    @Override
    public boolean isValid(Duration d, ConstraintValidatorContext constraintValidatorContext) {
        return (d == null) || (d.toMinutesPart() == 0 && d.toSecondsPart() == 0 && d.toMillisPart() == 0 && d.toNanosPart() == 0);
    }
}
