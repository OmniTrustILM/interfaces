package com.czertainly.api.exception;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectorException extends Exception implements PlatformException {

    protected ApiClientConnectorInfo connector;

    public ConnectorException() {
        super();
    }

    public ConnectorException(String message) {
        super(message);
    }

    public ConnectorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectorException(Throwable cause) {
        super(cause);
    }

    public ConnectorException(ApiClientConnectorInfo connector) {
        super();
        this.connector = connector;
    }

    public ConnectorException(String message, ApiClientConnectorInfo connector) {
        super(message);
        this.connector = connector;
    }

    public ConnectorException(String message, Throwable cause, ApiClientConnectorInfo connector) {
        super(message, cause);
        this.connector = connector;
    }

    public ConnectorException(Throwable cause, ApiClientConnectorInfo connector) {
        super(cause);
        this.connector = connector;
    }
}
