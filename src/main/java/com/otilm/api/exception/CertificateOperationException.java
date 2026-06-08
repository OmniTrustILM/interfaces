package com.otilm.api.exception;

public class CertificateOperationException extends Exception implements PlatformException {

    public CertificateOperationException() {
        super();
    }

    public CertificateOperationException(String message) {
        super(message);
    }

}
