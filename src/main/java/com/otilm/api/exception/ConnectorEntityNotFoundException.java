package com.otilm.api.exception;

import com.otilm.api.model.core.connector.ConnectorDto;

public class ConnectorEntityNotFoundException extends ConnectorException {

    public ConnectorEntityNotFoundException() {
        super();
    }

    public ConnectorEntityNotFoundException(String message) {
        super(message);
    }

    public ConnectorEntityNotFoundException(String objectType, Object identifier) {
        super("Object of type '" + objectType + "' identified by " + identifier + " is not found.");
    }

    public ConnectorEntityNotFoundException(Class<?> objectClass, Object identifier) {
        this(objectClass.getSimpleName(), identifier);
    }

    public ConnectorEntityNotFoundException(ConnectorDto connector) {
        super(connector);
    }

    public ConnectorEntityNotFoundException(String message, ConnectorDto connector) {
        super(message, connector);
    }

    public ConnectorEntityNotFoundException(String objectType, Object identifier, ConnectorDto connector) {
        this(objectType, identifier);
        super.connector = connector;
    }

    public ConnectorEntityNotFoundException(Class<?> objectClass, Object identifier, ConnectorDto connector) {
        this(objectClass.getSimpleName(), identifier, connector);
    }

}
