package com.otilm.core.deprecated;

import com.otilm.api.model.common.attribute.v1.AttributeDefinition;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class AttributeDefinitionUtils {

    private static final ObjectMapper ATTRIBUTES_OBJECT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static String serialize(List<AttributeDefinition> attributes) {
        if (attributes == null) {
            return null;
        }
        try {
            return ATTRIBUTES_OBJECT_MAPPER.writeValueAsString(attributes);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static List<AttributeDefinition> deserialize(String attributesJson) {
        if (attributesJson == null) {
            return null;
        }
        try {
            return ATTRIBUTES_OBJECT_MAPPER.readValue(attributesJson, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}