package com.otilm.api.config.serializer;

import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import com.otilm.api.model.common.attribute.v3.MetadataAttributeV3;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class MetadataAttributeDeserializer extends JsonDeserializer<MetadataAttribute> {
    @Override
    public MetadataAttribute deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        String version = node.has("version") ? node.get("version").asText() : "2";

        Class<? extends MetadataAttribute> valueType = null;
        if (version.equals("2")) {
            valueType = MetadataAttributeV2.class;
        }
        if (version.equals("3")) {
            valueType = MetadataAttributeV3.class;
        }

        return jp.getCodec().treeToValue(node, valueType);
    }
}
