package com.otilm.api.exception;

public class LocationException extends Exception implements PlatformException {

    public LocationException() {
        super();
    }

    public LocationException(String message) {
        super(message);
    }

}
