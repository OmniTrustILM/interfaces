package com.czertainly.api.model.core.signing.workflow.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class OidListValidator implements ConstraintValidator<ValidOidList, List<String>> {

    @Override
    public boolean isValid(List<String> oids, ConstraintValidatorContext constraintValidatorContext) {
        // we do not validate null values
        if (oids == null) {
            return true;
        }
        return oids.stream().allMatch(OidValidator::isValidOid);
    }

}
