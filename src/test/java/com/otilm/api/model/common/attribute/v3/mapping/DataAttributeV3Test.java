package com.otilm.api.model.common.attribute.v3.mapping;

import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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
    void dataAttributeV3_nullFieldMappingAndValueSource_omittedFromJson() {
        DataAttributeV3 attr = new DataAttributeV3();
        attr.setName("test");
        attr.setContentType(AttributeContentType.STRING);

        JsonNode json = mapper.valueToTree(attr);

        assertFalse(json.has("fieldMapping"));
        assertFalse(json.has("valueSource"));
    }

    @Test
    void fieldMapping_survivesBaseAttributeSerializerRoundTrip() throws Exception {
        RdnMappedField rdnField = new RdnMappedField();
        rdnField.setFieldType(FieldType.RDN);
        rdnField.setRdn("CN");

        FieldMapping fm = new FieldMapping();
        fm.setObjectType(ObjectType.X509_CERTIFICATE);
        fm.setFields(List.of(rdnField));

        DataAttributeV3 attr = new DataAttributeV3();
        attr.setUuid("test-uuid");
        attr.setName("commonName");
        attr.setType(AttributeType.DATA);
        attr.setContentType(AttributeContentType.STRING);
        attr.setFieldMapping(fm);

        String json = mapper.writerFor(BaseAttribute.class).writeValueAsString(attr);
        DataAttributeV3 result = (DataAttributeV3) mapper.readValue(json, BaseAttribute.class);

        assertNotNull(result.getFieldMapping());
        assertEquals(1, result.getFieldMapping().getFields().size());
        assertEquals(FieldType.RDN, result.getFieldMapping().getFields().get(0).getFieldType());
        assertEquals("CN", ((RdnMappedField) result.getFieldMapping().getFields().get(0)).getRdn());
    }

}
