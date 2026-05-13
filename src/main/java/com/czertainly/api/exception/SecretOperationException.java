package com.czertainly.api.exception;

public class SecretOperationException extends Exception implements PlatformException {

    public SecretOperationException(String message) {
        super(message);
    }
}
