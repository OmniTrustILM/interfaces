package com.czertainly.api.model.client.signing.profile.workflow.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class OidValidator implements ConstraintValidator<ValidOid, String> {

    private static final Pattern OID_REGEX = Pattern.compile("^[0-2](\\.(0|[1-9]\\d*)){1,50}$");

    @Override
    public boolean isValid(String oid, ConstraintValidatorContext constraintValidatorContext) {
        // we do not validate null values
        if (oid == null) {
            return true;
        }
        return isValidOid(oid);
    }

    static boolean isValidOid(String oid) {
        return OID_REGEX.matcher(oid).matches();
    }
}
