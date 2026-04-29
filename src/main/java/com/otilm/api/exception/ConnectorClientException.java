package com.otilm.api.exception;

import com.otilm.api.model.core.connector.ConnectorDto;
import org.springframework.http.HttpStatus;

public class ConnectorClientException extends ConnectorException {
    private HttpStatus httpStatus;

    public ConnectorClientException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ConnectorClientException(HttpStatus httpStatus, ConnectorDto connector) {
        super(connector);
        this.httpStatus = httpStatus;
    }

    public ConnectorClientException(String message, HttpStatus httpStatus, ConnectorDto connector) {
        super(message, connector);
        this.httpStatus = httpStatus;
    }

    public ConnectorClientException(String message, Throwable cause, HttpStatus httpStatus, ConnectorDto connector) {
        super(message, cause, connector);
        this.httpStatus = httpStatus;
    }

    public ConnectorClientException(Throwable cause, HttpStatus httpStatus, ConnectorDto connector) {
        super(cause, connector);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
