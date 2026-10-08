package com.otilm.api.config.jackson3;

import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.common.CustomAttribute;
import com.otilm.api.model.common.attribute.common.DataAttribute;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v2.GroupAttributeV2;
import com.otilm.api.model.common.attribute.v2.InfoAttributeV2;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.common.attribute.v3.GroupAttributeV3;
import com.otilm.api.model.common.attribute.v3.InfoAttributeV3;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/** Jackson 3 twin of {@link com.otilm.api.config.serializer.BaseAttributeSerializer}. */
final class BaseAttributeSerializer extends ValueSerializer<BaseAttribute> {

    private static final String CONTENT_TYPE = "contentType";
    private static final String PROPERTIES = "properties";
    private static final String ATTRIBUTE_CALLBACK = "attributeCallback";

    @Override
    public void serialize(BaseAttribute value, JsonGenerator gen, SerializationContext context) {
        gen.writeStartObject();
        gen.writeStringProperty("type", value.getType().getCode());
        gen.writeNumberProperty("version", value.getVersion());
        gen.writeStringProperty("uuid", value.getUuid());
        gen.writeStringProperty("name", value.getName());
        gen.writeStringProperty("description", value.getDescription());
        context.defaultSerializeProperty("content", value.getContent(), gen);
        switch (value.getType()) {
            case DATA -> {
                DataAttribute attribute = (DataAttribute) value;
                writeContentTypeAndProperties(attribute.getContentType(), attribute.getProperties(), gen, context);
                context.defaultSerializeProperty(ATTRIBUTE_CALLBACK, attribute.getAttributeCallback(), gen);
                context.defaultSerializeProperty("constraints", attribute.getConstraints(), gen);
                if (attribute instanceof DataAttributeV3 v3) {
                    context.defaultSerializeProperty("fieldMapping", v3.getFieldMapping(), gen);
                    context.defaultSerializeProperty("valueSource", v3.getValueSource(), gen);
                }
            }
            case GROUP -> context
                    .defaultSerializeProperty(ATTRIBUTE_CALLBACK,
                            value.getVersion() == 2
                                    ? ((GroupAttributeV2) value).getAttributeCallback()
                                    : ((GroupAttributeV3) value).getAttributeCallback(),
                            gen);
            case INFO -> {
                if (value.getVersion() == 2) {
                    InfoAttributeV2 attribute = (InfoAttributeV2) value;
                    writeContentTypeAndProperties(attribute.getContentType(), attribute.getProperties(), gen, context);
                } else {
                    InfoAttributeV3 attribute = (InfoAttributeV3) value;
                    writeContentTypeAndProperties(attribute.getContentType(), attribute.getProperties(), gen, context);
                }
            }
            case META -> {
                MetadataAttribute attribute = (MetadataAttribute) value;
                writeContentTypeAndProperties(attribute.getContentType(), attribute.getProperties(), gen, context);
            }
            case CUSTOM -> {
                CustomAttribute attribute = (CustomAttribute) value;
                writeContentTypeAndProperties(attribute.getContentType(), attribute.getProperties(), gen, context);
            }
        }
        gen.writeEndObject();
    }

    @Override
    public Class<BaseAttribute> handledType() {
        return BaseAttribute.class;
    }

    private static void writeContentTypeAndProperties(AttributeContentType contentType, Object properties,
            JsonGenerator gen, SerializationContext context) {
        gen.writeStringProperty(CONTENT_TYPE, contentType.getCode());
        context.defaultSerializeProperty(PROPERTIES, properties, gen);
    }
}
