package com.otilm.api.model.client.cmp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.stream.Collectors;

public class CmpProfileNameValidator implements ConstraintValidator<ValidCmpProfileName, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !containsUnreservedCharacters(s);
    }

    private static boolean containsUnreservedCharacters(final String value) {
        return !value.chars()
                .filter(c -> !isUnreserved((char) c))
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining()).isEmpty();
    }

    private static boolean isUnreserved(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c >= '0' && c <= '9') ||
                c == '-' || c == '.' || c == '_' || c == '~';
    }
}
