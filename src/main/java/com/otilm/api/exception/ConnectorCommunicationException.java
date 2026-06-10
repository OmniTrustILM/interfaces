package com.otilm.api.exception;

import com.otilm.api.clients.ApiClientConnectorInfo;

public class ConnectorCommunicationException extends ConnectorException {

    public ConnectorCommunicationException(String message, ApiClientConnectorInfo connector) {
        super(message, connector);
    }

    public ConnectorCommunicationException(String message, Throwable cause, ApiClientConnectorInfo connector) {
        super(message, cause, connector);
    }
}
