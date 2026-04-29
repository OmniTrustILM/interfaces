package com.otilm.api.exception;

import java.util.regex.Matcher;

public class ValidationError {

    private String errorDescriptionTemplate;
    private Object[] errorDescriptionObjects;

    public ValidationError(String errorDescriptionTemplate, Object... errorDescriptionObjects) {
        this.errorDescriptionTemplate = errorDescriptionTemplate;
        this.errorDescriptionObjects = errorDescriptionObjects;
    }

    public String getErrorDescription() {
        return formatDescription(this.errorDescriptionTemplate, this.errorDescriptionObjects);
    }

    public static ValidationError create(String errorDescriptionTemplate, Object... errorDescriptionObjects) {
        return new ValidationError(errorDescriptionTemplate, errorDescriptionObjects);
    }

    private static String formatDescription(String template, Object... objects) {
        String message = template;
        for (Object object : objects) {
            message = message.replaceFirst("\\{\\}", object != null ? Matcher.quoteReplacement(object.toString()) : "null");
        }
        return message;
    }

    @Override
    public String toString() {
        return getErrorDescription();
    }
}
