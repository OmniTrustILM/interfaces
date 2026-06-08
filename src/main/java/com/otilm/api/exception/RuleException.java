package com.otilm.api.exception;

public class RuleException extends Exception implements PlatformException {

    public RuleException(String message) {
        super(message);
    }

}
