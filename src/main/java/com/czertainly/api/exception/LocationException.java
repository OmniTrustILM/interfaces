package com.czertainly.api.exception;

public class LocationException extends Exception implements PlatformException {

    public LocationException() {
        super();
    }

    public LocationException(String message) {
        super(message);
    }

}
