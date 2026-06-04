package com.czertainly.api.model.common.validation;

import com.czertainly.api.model.core.signing.TspAuthenticationMethod;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;

public class BasicCredentialsRequiredIfBasicPasswordValidator
        implements ConstraintValidator<BasicCredentialsRequiredIfBasicPassword, BasicPasswordConstrained> {

    @Override
    public boolean isValid(BasicPasswordConstrained value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        Collection<TspAuthenticationMethod> methods = value.getAllowedAuthenticationMethods();
        if (methods == null || !methods.contains(TspAuthenticationMethod.BASIC_PASSWORD)) {
            return true;
        }
        Collection<?> credentials = value.getBasicCredentials();
        if (credentials == null || credentials.isEmpty()) {
            return false;
        }
        return value.getVaultProfileUuid() != null;
    }
}
