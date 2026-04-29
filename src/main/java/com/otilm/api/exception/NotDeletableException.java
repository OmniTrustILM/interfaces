package com.otilm.api.exception;

public class NotDeletableException extends Exception {

    private final String objectType;

    public NotDeletableException(String objectType, Object identifier) {
        super("Object '" + objectType + "' identified by " + identifier + " can not be deleted.");
        this.objectType = objectType;
    }

    public NotDeletableException(Class<?> objectClass, Object identifier) {
        this(objectClass.getSimpleName(), identifier);
    }

    public String getObjectType() {
        return objectType;
    }
}
