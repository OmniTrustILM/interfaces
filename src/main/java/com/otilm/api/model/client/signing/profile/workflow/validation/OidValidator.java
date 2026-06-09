package com.otilm.api.model.client.signing.profile.workflow.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class OidValidator implements ConstraintValidator<ValidOid, String> {

    /** ASN.1 OID: first arc 0/1 requires second arc in 0..39; first arc 2 is unrestricted. */
    private static final Pattern OID_REGEX = Pattern.compile(
            "^([01]\\.(\\d|[1-3]\\d)|2\\.(0|[1-9]\\d*))(\\.(0|[1-9]\\d*)){0,49}$");

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
