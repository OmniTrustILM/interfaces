package com.otilm.api.model.client.discovery.validation;

import com.otilm.api.model.client.discovery.DiscoveryWindowDto;
import com.otilm.api.model.client.discovery.DiscoveryWindowMode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Duration;

public class ConsistentLookbackValidator implements ConstraintValidator<ConsistentLookback, DiscoveryWindowDto> {

    private static final String REPORTED_PROPERTY = "lookback";

    @Override
    public boolean isValid(DiscoveryWindowDto value, ConstraintValidatorContext context) {
        if (value == null || isConsistent(value.getMode(), value.getLookback())) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context
                .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(REPORTED_PROPERTY)
                .addConstraintViolation();
        return false;
    }

    private static boolean isConsistent(DiscoveryWindowMode mode, Duration lookback) {
        if (mode == DiscoveryWindowMode.LOOKBACK) {
            return lookback != null && lookback.isPositive();
        }
        return lookback == null;
    }
}
