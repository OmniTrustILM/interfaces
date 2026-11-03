package com.otilm.api.model.common.validation;

import jakarta.validation.ConstraintValidator;

public class NullableNotBlankValidator implements ConstraintValidator<NullableNotBlank, String> {

    /** Blank test of {@link NullableNotBlank}; missing text counts as blank. */
    public static boolean readsAsNoValue(String value) {
        return value == null || value.isBlank() || value.trim().isEmpty();
    }

    @Override
    public boolean isValid(String value, jakarta.validation.ConstraintValidatorContext context) {
        return value == null || !readsAsNoValue(value);
    }
}
