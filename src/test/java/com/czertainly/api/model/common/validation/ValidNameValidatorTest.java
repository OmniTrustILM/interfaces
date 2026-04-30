package com.czertainly.api.model.common.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidNameValidatorTest {

    private final ValidNameValidator validator = new ValidNameValidator();

    @Test
    void nullIsValid() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void emptyStringIsValid() {
        assertTrue(validator.isValid("", null));
    }

    @Test
    void validUnreservedCharsAreValid() {
        assertTrue(validator.isValid("NTP-Config-1", null));
        assertTrue(validator.isValid("my.config_name~test", null));
        assertTrue(validator.isValid("ABC123", null));
    }

    @Test
    void spacesAreInvalid() {
        assertFalse(validator.isValid("name with spaces", null));
    }

    @Test
    void reservedUriCharsAreInvalid() {
        assertFalse(validator.isValid("name@domain", null));
        assertFalse(validator.isValid("name/path", null));
        assertFalse(validator.isValid("name#anchor", null));
        assertFalse(validator.isValid("name?query", null));
    }
}
