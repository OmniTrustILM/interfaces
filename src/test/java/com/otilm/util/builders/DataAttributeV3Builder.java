package com.otilm.util.builders;

import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.DataAttributeProperties;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;

/**
 * Builds a minimal, valid {@link DataAttributeV3} for tests. With no overrides {@link #build()}
 * yields a STRING attribute whose properties carry a label equal to the attribute name. Override
 * only the field a scenario is about; the defaults carry the rest.
 */
public final class DataAttributeV3Builder {

    private String uuid = "00000000-0000-0000-0000-000000000001";
    private String name = "test-attribute";

    public static DataAttributeV3Builder aDataAttribute() {
        return new DataAttributeV3Builder();
    }

    public DataAttributeV3Builder withUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public DataAttributeV3Builder withName(String name) {
        this.name = name;
        return this;
    }

    public DataAttributeV3 build() {
        DataAttributeV3 attribute = new DataAttributeV3();
        attribute.setUuid(uuid);
        attribute.setName(name);
        attribute.setContentType(AttributeContentType.STRING);
        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setLabel(name);
        attribute.setProperties(properties);
        return attribute;
    }
}
