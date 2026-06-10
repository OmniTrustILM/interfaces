package com.otilm.api.config.serializer;

import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.v2.*;
import com.otilm.api.model.common.attribute.v3.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class BaseAttributeDeserializer extends JsonDeserializer<BaseAttribute> {
    @Override
    public BaseAttribute deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        String version = node.has("version") ? node.get("version").asText() : "2";
        String attributeTypeCode = node.has("type") ? node.get("type").asText() : null;

        if (attributeTypeCode == null || version == null) {
            throw new IllegalArgumentException("Missing required fields: type or version");
        }

        AttributeType attributeType = AttributeType.fromCode(attributeTypeCode);

        Class<? extends BaseAttribute> valueType = null;

        if (version.equals("2")) {
            switch (attributeType) {
                case META -> valueType = MetadataAttributeV2.class;
                case DATA -> valueType = DataAttributeV2.class;
                case GROUP -> valueType = GroupAttributeV2.class;
                case CUSTOM -> valueType = CustomAttributeV2.class;
                case INFO -> valueType = InfoAttributeV2.class;
            }
        }
        else if (version.equals("3")) {
            switch (attributeType) {
                case META -> valueType = MetadataAttributeV3.class;
                case DATA -> valueType = DataAttributeV3.class;
                case GROUP -> valueType = GroupAttributeV3.class;
                case CUSTOM -> valueType = CustomAttributeV3.class;
                case INFO -> valueType = InfoAttributeV3.class;
            }
        } else {
            throw new IllegalArgumentException("Unsupported Attribute version: " + version);
        }


        return jp.getCodec().treeToValue(node, valueType);
    }
}
