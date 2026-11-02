package com.otilm.api.model.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NullableNotBlankValidatorTest {

    private final NullableNotBlankValidator validator = new NullableNotBlankValidator();

    @Test
    void nullIsValid() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void textWithSurroundingSpacesIsValid() {
        assertTrue(validator.isValid(" a ", null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t\n", "\u0000", "\u2003", " \u2003 "})
    void blankTextIsInvalid(String value) {
        assertFalse(validator.isValid(value, null));
    }
}
