package com.czertainly.api.model.core.signing.workflow.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OidValidator implements ConstraintValidator<ValidOid, String> {

    private static final String OID_REGEX = "^[0-2](\\.(0|[1-9]\\d*)){1,50}$";

    @Override
    public boolean isValid(String oid, ConstraintValidatorContext constraintValidatorContext) {
        // we do not validate null values
        if (oid == null) {
            return true;
        }
        return isValidOid(oid);
    }

    static boolean isValidOid(String oid) {
        return oid.matches(OID_REGEX);
    }

}
