package com.otilm.api.model.connector.signatures.contentsigning.common.validation;

import com.otilm.api.model.common.signature.parameters.SignatureParametersDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ComputeDtbsRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ParametersMatchFamilyValidator
        implements
            ConstraintValidator<ParametersMatchFamily, ComputeDtbsRequestDto> {

    private static final String REPORTED_PROPERTY = "signatureParameters";

    @Override
    public boolean isValid(ComputeDtbsRequestDto value, ConstraintValidatorContext context) {
        if (value == null || !familiesConflict(value)) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context
                .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(REPORTED_PROPERTY)
                .addConstraintViolation();
        return false;
    }

    /** An absent family is left to its own {@code @NotNull} constraint. */
    private static boolean familiesConflict(ComputeDtbsRequestDto value) {
        SignatureParametersDto parameters = value.getSignatureParameters();
        return parameters != null && value.getFamily() != null && !value.getFamily().equals(parameters.getFamily());
    }
}
