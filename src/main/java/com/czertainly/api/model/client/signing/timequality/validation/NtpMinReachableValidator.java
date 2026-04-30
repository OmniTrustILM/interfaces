package com.czertainly.api.model.client.signing.timequality.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NtpMinReachableValidator implements ConstraintValidator<ValidNtpMinReachable, NtpConfiguration> {

    @Override
    public boolean isValid(NtpConfiguration value, ConstraintValidatorContext context) {
        if (value == null || value.getNtpServers() == null) {
            return true;
        }
        if (value.getNtpServersMinReachable() > value.getNtpServers().size()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("ntpServersMinReachable")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
