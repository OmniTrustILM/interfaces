package com.otilm.api.model.client.cmp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.UUID;

public class UuidValidator implements ConstraintValidator<ValidUuid, String> {

    @Override
    public boolean isValid(String uuid, ConstraintValidatorContext constraintValidatorContext) {
        // we do not validate null values
        if (uuid == null) {
            return true;
        }
        return isValidUuid(uuid);
    }

    private static boolean isValidUuid(String uuid) {
        return UUID.fromString(uuid).toString().equals(uuid);
    }

}
