package com.czertainly.api.model.client.signing.profile.workflow.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class OidValidator implements ConstraintValidator<ValidOid, String> {

    // ASN.1 rule: when the first arc is 0 or 1 the second arc must be 0..39;
    // when the first arc is 2 the second arc is unrestricted.
    private static final Pattern OID_REGEX = Pattern.compile(
            "^([01]\\.(0|[1-9]|[1-3][0-9])|2\\.(0|[1-9]\\d*))(\\.(0|[1-9]\\d*)){0,49}$");

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
