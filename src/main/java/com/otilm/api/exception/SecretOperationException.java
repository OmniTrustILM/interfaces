package com.otilm.api.exception;

public class SecretOperationException extends Exception implements PlatformException {

    public SecretOperationException(String message) {
        super(message);
    }
}
