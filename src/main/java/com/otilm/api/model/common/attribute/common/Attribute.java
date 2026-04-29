package com.otilm.api.model.common.attribute.common;


import java.io.Serializable;

public interface Attribute extends Serializable {

    String getUuid();

    String getName();
    AttributeType getType();

    int getVersion();
}
