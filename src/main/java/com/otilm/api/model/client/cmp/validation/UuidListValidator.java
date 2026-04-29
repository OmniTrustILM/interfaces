package com.otilm.api.model.client.cmp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UuidListValidator implements ConstraintValidator<ValidUuidList, List<String>> {

    @Override
    public boolean isValid(List<String> uuids, ConstraintValidatorContext constraintValidatorContext) {
        // we do not validate null values
        if (uuids == null) {
            return true;
        }
        return uuids.stream().allMatch(UuidListValidator::isValidUuid);
    }

    private static boolean isValidUuid(String uuid) {
        return UUID.fromString(uuid).toString().equals(uuid);
    }

}
