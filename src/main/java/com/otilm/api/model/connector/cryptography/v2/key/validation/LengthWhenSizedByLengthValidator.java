package com.otilm.api.model.connector.cryptography.v2.key.validation;

import com.otilm.api.model.connector.cryptography.v2.key.KeyDataV2Dto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LengthWhenSizedByLengthValidator implements ConstraintValidator<LengthWhenSizedByLength, KeyDataV2Dto> {

    @Override
    public boolean isValid(KeyDataV2Dto value, ConstraintValidatorContext context) {
        if (value == null || value.getAlgorithm() == null || value.getLength() != null
                || !value.getAlgorithm().isSizedByLength()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context
                .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("length")
                .addConstraintViolation();
        return false;
    }
}
