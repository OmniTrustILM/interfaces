package com.otilm.api.model.common.attribute.v3.mapping;

import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataAttributeV3Test {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    void dataAttributeV3_withoutFieldMapping_deserializes() throws Exception {
        String json = """
                {
                    "name": "test",
                    "description": "Test attribute",
                    "contentType": "string"
                }
                """;

        DataAttributeV3 result = mapper.readValue(json, DataAttributeV3.class);

        assertEquals("test", result.getName());
        assertEquals(AttributeContentType.STRING, result.getContentType());
        assertNull(result.getFieldMapping());
        assertNull(result.getValueSource());
    }

    @Test
    void dataAttributeV3_nullFieldMappingAndValueSource_omittedFromJson() throws Exception {
        DataAttributeV3 attr = new DataAttributeV3();
        attr.setName("test");
        attr.setContentType(AttributeContentType.STRING);

        JsonNode json = mapper.valueToTree(attr);

        assertFalse(json.has("fieldMapping"));
        assertFalse(json.has("valueSource"));
    }

}
