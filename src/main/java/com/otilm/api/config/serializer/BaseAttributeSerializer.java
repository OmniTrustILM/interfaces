package com.otilm.api.config.serializer;

import com.otilm.api.model.common.attribute.common.*;
import com.otilm.api.model.common.attribute.v2.GroupAttributeV2;
import com.otilm.api.model.common.attribute.v2.InfoAttributeV2;
import com.otilm.api.model.common.attribute.v3.GroupAttributeV3;
import com.otilm.api.model.common.attribute.v3.InfoAttributeV3;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class BaseAttributeSerializer
        extends JsonSerializer<BaseAttribute> {

    public static final String CONTENT_TYPE = "contentType";
    public static final String ATTR_PROPERTIES = "properties";
    public static final String ATTRIBUTE_CALLBACK = "attributeCallback";
    public static final String CONSTRAINTS = "constraints";

    @Override
    public void serialize(BaseAttribute value,
                          JsonGenerator gen,
                          SerializerProvider serializers)
            throws IOException {

        gen.writeStartObject();

        gen.writeStringField("type", value.getType().getCode());
        gen.writeNumberField("version", value.getVersion());
        gen.writeStringField("uuid", value.getUuid());
        gen.writeStringField("name", value.getName());
        gen.writeStringField("description", value.getDescription());

        gen.writeFieldName("content");
        serializers.defaultSerializeValue(value.getContent(), gen);


        switch (value.getType()) {
            case DATA -> {
                DataAttribute attribute = (DataAttribute) value;
                gen.writeStringField(CONTENT_TYPE, attribute.getContentType().getCode());
                gen.writeFieldName(ATTR_PROPERTIES);
                serializers.defaultSerializeValue(attribute.getProperties(), gen);
                gen.writeFieldName(ATTRIBUTE_CALLBACK);
                serializers.defaultSerializeValue(attribute.getAttributeCallback(), gen);
                gen.writeFieldName(CONSTRAINTS);
                serializers.defaultSerializeValue(attribute.getConstraints(), gen);
                gen.writeEndObject();
            }
            case GROUP -> {
                if (value.getVersion() == 2) {
                    GroupAttributeV2 attribute = (GroupAttributeV2) value;
                    gen.writeFieldName(ATTRIBUTE_CALLBACK);
                    serializers.defaultSerializeValue(attribute.getAttributeCallback(), gen);
                    gen.writeEndObject();
                }
                if (value.getVersion() == 3) {
                    GroupAttributeV3 attribute = (GroupAttributeV3) value;
                    gen.writeFieldName(ATTRIBUTE_CALLBACK);
                    serializers.defaultSerializeValue(attribute.getAttributeCallback(), gen);
                    gen.writeEndObject();
                }
            }
            case INFO -> {
                if (value.getVersion() == 2) {
                    InfoAttributeV2 attribute = (InfoAttributeV2) value;
                    gen.writeStringField(CONTENT_TYPE, attribute.getContentType().getCode());
                    gen.writeFieldName(ATTR_PROPERTIES);
                    serializers.defaultSerializeValue(attribute.getProperties(), gen);
                    gen.writeEndObject();
                }
                if (value.getVersion() == 3) {
                    InfoAttributeV3 attribute = (InfoAttributeV3) value;
                    gen.writeStringField(CONTENT_TYPE, attribute.getContentType().getCode());
                    gen.writeFieldName(ATTR_PROPERTIES);
                    serializers.defaultSerializeValue(attribute.getProperties(), gen);
                    gen.writeEndObject();
                }
            }
            case META -> {
                MetadataAttribute attribute = (MetadataAttribute) value;
                gen.writeStringField(CONTENT_TYPE, attribute.getContentType().getCode());
                gen.writeFieldName(ATTR_PROPERTIES);
                serializers.defaultSerializeValue(attribute.getProperties(), gen);
                gen.writeEndObject();
            }
            case CUSTOM -> {
                CustomAttribute attribute = (CustomAttribute) value;
                gen.writeStringField(CONTENT_TYPE, attribute.getContentType().getCode());
                gen.writeFieldName(ATTR_PROPERTIES);
                serializers.defaultSerializeValue(attribute.getProperties(), gen);
                gen.writeEndObject();
            }
        }
    }

    @Override
    public Class<BaseAttribute> handledType() {
        return BaseAttribute.class;
    }

}

