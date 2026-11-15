package com.otilm.api.model.connector.signatures.contentsigning.declaration.validation;

import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.connector.signatures.contentsigning.declaration.SignatureFamilyDeclarationDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UniqueFamilyCodesValidator
        implements
            ConstraintValidator<UniqueFamilyCodes, List<SignatureFamilyDeclarationDto>> {

    @Override
    public boolean isValid(List<SignatureFamilyDeclarationDto> families, ConstraintValidatorContext context) {
        if (families == null) {
            return true;
        }
        boolean valid = true;
        Map<SignatureFamily, Integer> firstIndexByCode = new HashMap<>();
        for (int index = 0; index < families.size(); index++) {
            SignatureFamilyDeclarationDto family = families.get(index);
            if (family == null || family.getCode() == null) {
                continue;
            }
            Integer previousIndex = firstIndexByCode.putIfAbsent(family.getCode(), index);
            if (previousIndex != null) {
                context.disableDefaultConstraintViolation();
                context
                        .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()
                                + "; duplicates the family at index " + previousIndex)
                        .addPropertyNode("code")
                        .inIterable()
                        .atIndex(index)
                        .addConstraintViolation();
                valid = false;
            }
        }
        return valid;
    }
}
