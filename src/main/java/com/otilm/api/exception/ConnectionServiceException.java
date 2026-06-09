package com.otilm.api.exception;

import org.springframework.http.HttpStatus;

public class ConnectionServiceException extends ConnectorException {
    private HttpStatus httpStatus;

    public ConnectionServiceException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
