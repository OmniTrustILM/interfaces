package com.czertainly.api.model.common.validation;

import jakarta.validation.ConstraintValidator;

public class NullableNotBlankValidator implements ConstraintValidator<NullableNotBlank, String> {

    @Override
    public boolean isValid(String value, jakarta.validation.ConstraintValidatorContext context) {
        return value == null || !value.trim().isEmpty();
    }
}
