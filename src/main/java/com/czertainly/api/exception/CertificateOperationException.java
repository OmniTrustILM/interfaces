package com.czertainly.api.exception;

public class CertificateOperationException extends Exception implements PlatformException {

    public CertificateOperationException() {
        super();
    }

    public CertificateOperationException(String message) {
        super(message);
    }

}
