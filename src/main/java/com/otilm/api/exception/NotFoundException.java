package com.otilm.api.exception;

import com.otilm.api.model.core.connector.ConnectorDto;
import lombok.Getter;

@Getter
public class NotFoundException extends Exception {

    private ConnectorDto connector;

    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String objectType, Object identifier) {
        super("Object of type '" + objectType + "' identified by " + identifier + " is not found.");
    }

    public NotFoundException(Class<?> objectClass, Object identifier) {
        this(objectClass.getSimpleName(), identifier);
    }

    public NotFoundException(ConnectorDto connector) {
        this.connector = connector;
    }

    public NotFoundException(String message, ConnectorDto connector) {
        super(message);
        this.connector = connector;
    }

    public NotFoundException(String objectType, Object identifier, ConnectorDto connector) {
        this(objectType, identifier);
        this.connector = connector;
    }

    public NotFoundException(Class<?> objectClass, Object identifier, ConnectorDto connector) {
        this(objectClass.getSimpleName(), identifier, connector);
    }
}
