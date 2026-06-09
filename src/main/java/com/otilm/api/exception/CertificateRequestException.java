package com.otilm.api.exception;

public class CertificateRequestException extends Exception implements PlatformException {

    public CertificateRequestException(String message) {
        super(message);
    }

    public CertificateRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
