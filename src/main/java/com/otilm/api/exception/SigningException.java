package com.otilm.api.exception;

/**
 * Its message is curated client text, which {@link PlatformException#safeMessage(Throwable, String)} passes on
 * unchanged.
 */
public class SigningException extends Exception implements PlatformException {

    public SigningException(String clientMessage, Throwable cause) {
        super(clientMessage, cause);
    }
}
