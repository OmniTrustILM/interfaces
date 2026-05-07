package com.czertainly.api.model.client.signing.timequality.validation;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class PositiveDurationValidatorTest {

    private final PositiveDurationValidator validator = new PositiveDurationValidator();

    @Test
    void nullIsValid() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void positiveDurationIsValid() {
        assertTrue(validator.isValid(Duration.ofMillis(1), null));
        assertTrue(validator.isValid(Duration.ofSeconds(30), null));
    }

    @Test
    void zeroIsInvalid() {
        assertFalse(validator.isValid(Duration.ZERO, null));
    }

    @Test
    void negativeIsInvalid() {
        assertFalse(validator.isValid(Duration.ofSeconds(-1), null));
        assertFalse(validator.isValid(Duration.ofMillis(-1), null));
    }
}
