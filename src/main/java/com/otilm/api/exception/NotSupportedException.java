package com.otilm.api.exception;

public class NotSupportedException extends RuntimeException implements PlatformException {

    public NotSupportedException(String message) {
        super(message);
    }

}
