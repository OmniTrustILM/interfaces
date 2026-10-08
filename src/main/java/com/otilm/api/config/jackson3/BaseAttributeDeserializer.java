package com.otilm.api.config.jackson3;

import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.v2.CustomAttributeV2;
import com.otilm.api.model.common.attribute.v2.DataAttributeV2;
import com.otilm.api.model.common.attribute.v2.GroupAttributeV2;
import com.otilm.api.model.common.attribute.v2.InfoAttributeV2;
import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import com.otilm.api.model.common.attribute.v3.CustomAttributeV3;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.common.attribute.v3.GroupAttributeV3;
import com.otilm.api.model.common.attribute.v3.InfoAttributeV3;
import com.otilm.api.model.common.attribute.v3.MetadataAttributeV3;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

/** Jackson 3 twin of {@link com.otilm.api.config.serializer.BaseAttributeDeserializer}. */
final class BaseAttributeDeserializer extends ValueDeserializer<BaseAttribute> {

    @Override
    public BaseAttribute deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);
        String version = node.has("version") ? node.get("version").asString() : "2";
        if (!node.has("type")) {
            throw new IllegalArgumentException("Missing required fields: type or version");
        }
        AttributeType type = AttributeType.fromCode(node.get("type").asString());
        Class<? extends BaseAttribute> valueType = switch (version) {
            case "2" -> switch (type) {
                case META -> MetadataAttributeV2.class;
                case DATA -> DataAttributeV2.class;
                case GROUP -> GroupAttributeV2.class;
                case CUSTOM -> CustomAttributeV2.class;
                case INFO -> InfoAttributeV2.class;
            };
            case "3" -> switch (type) {
                case META -> MetadataAttributeV3.class;
                case DATA -> DataAttributeV3.class;
                case GROUP -> GroupAttributeV3.class;
                case CUSTOM -> CustomAttributeV3.class;
                case INFO -> InfoAttributeV3.class;
            };
            default -> throw new IllegalArgumentException("Unsupported Attribute version: " + version);
        };
        return context.readTreeAsValue(node, valueType);
    }
}
