package com.czertainly.api.exception;

public class SchedulerException extends Exception implements PlatformException {

    public SchedulerException(String message) {
        super(message);
    }
}
