package com.czertainly.api.exception;

public class NotSupportedException extends RuntimeException implements PlatformException {

    public NotSupportedException(String message) {
        super(message);
    }

}
