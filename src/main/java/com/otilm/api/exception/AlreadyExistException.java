package com.otilm.api.exception;

public class AlreadyExistException extends Exception {

    public AlreadyExistException() {
        super();
    }

    public AlreadyExistException(String message) {
        super(message);
    }

    public AlreadyExistException(String objectType, Object identifier) {
        super("Object of type '" + objectType + "' identified by " + identifier + " already exists.");
    }

    public AlreadyExistException(Class<?> objectClass, Object identifier) {
        this(objectClass.getSimpleName(), identifier);
    }

    @Override
    public String toString() {
        return getLocalizedMessage();
    }
}
