package com.otilm.api.model.common.validation;

import com.otilm.api.model.core.signing.TspAuthenticationMethod;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;

public class VaultProfileRequiredForBasicPasswordValidator
        implements ConstraintValidator<VaultProfileRequiredForBasicPassword, VaultProfileConstrained> {

    @Override
    public boolean isValid(VaultProfileConstrained value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        Collection<TspAuthenticationMethod> methods = value.getAllowedAuthenticationMethods();
        if (methods == null || !methods.contains(TspAuthenticationMethod.BASIC_PASSWORD)) {
            return true;
        }
        boolean valid = value.getVaultProfileUuid() != null;
        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("vaultProfileUuid")
                    .addConstraintViolation();
        }
        return valid;
    }
}
