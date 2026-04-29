package com.otilm.api.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ValidationException extends RuntimeException {

    private ArrayList<ValidationError> errors = new ArrayList<>();

    public ValidationException(String message) {
        this(Collections.singletonList(ValidationError.create(message)));
    }

    public ValidationException(List<ValidationError> errors) {
        super(errorsToMessage(errors));
        this.errors = new ArrayList<>(errors);
    }

    public ValidationException(ValidationError error) {
        this(Collections.singletonList(error));
    }

    public ValidationException(String message, List<ValidationError> errors) {
        super(message + " \n" + errorsToMessage(errors));
        this.errors = new ArrayList<>(errors);
    }

    public ValidationException(String message, ValidationError error) {
        this(message, Collections.singletonList(error));
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    private static String errorsToMessage(List<ValidationError> errors) {
        return errors.stream()
                .map(ValidationError::getErrorDescription)
                .collect(Collectors.joining(" \n"));
    }

    @Override
    public String toString() {
        return getLocalizedMessage();
    }
}
