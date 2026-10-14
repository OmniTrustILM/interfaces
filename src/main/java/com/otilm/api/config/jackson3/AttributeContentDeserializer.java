package com.otilm.api.config.jackson3;

import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.v2.content.BaseAttributeContentV2;
import com.otilm.api.model.common.attribute.v3.content.BaseAttributeContentV3;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

/** Jackson 3 twin of {@link com.otilm.api.config.serializer.AttributeContentDeserializer}. */
final class AttributeContentDeserializer extends ValueDeserializer<AttributeContent> {

    @Override
    public AttributeContent deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);
        JsonNode contentType = node.get("contentType");
        return contentType != null && !contentType.isNull()
                ? context.readTreeAsValue(node, BaseAttributeContentV3.class)
                : context.readTreeAsValue(node, BaseAttributeContentV2.class);
    }
}
