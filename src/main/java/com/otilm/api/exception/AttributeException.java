package com.otilm.api.exception;

import com.otilm.api.model.common.attribute.common.AttributeType;
import lombok.Getter;

@Getter
public class AttributeException extends Exception {

    private String uuid;
    private String name;
    private AttributeType type;
    private String connectorUuid;

    public AttributeException() {
        super();
    }

    public AttributeException(String message) {
        super(message);
    }

    public AttributeException(String message, String uuid, String name, AttributeType type, String connectorUuid) {
        super(String.format("%s|Attribute[UUID=%s, Name=%s, Type=%s, ConnectorUUID=%s]", message, uuid, name, type.getLabel(), connectorUuid));
        this.uuid = uuid;
        this.name = name;
        this.type = type;
        this.connectorUuid = connectorUuid;
    }
}
