package com.czertainly.api.exception;

public class CertificateException extends Exception implements PlatformException {

    public CertificateException() {
        super();
    }

    public CertificateException(String message) {
        super(message);
    }

}
