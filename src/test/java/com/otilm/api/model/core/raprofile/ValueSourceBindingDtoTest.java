package com.otilm.api.model.core.raprofile;

import com.otilm.api.model.common.attribute.v3.mapping.SourceParam;
import com.otilm.api.model.common.attribute.v3.mapping.ValueSourceType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class ValueSourceBindingDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void bindingByUuidRoundTrips() throws Exception {
        SourceParam p = new SourceParam();
        p.setAttributeName("dc_select");

        ValueSourceBindingDto dto = new ValueSourceBindingDto();
        dto.setAttributeUuid("u1");
        dto.setValueSourceType(ValueSourceType.STATIC_LIST);
        dto.setCollectionRef("cmdb.servers");
        dto.setParams(List.of(p));

        String json = mapper.writeValueAsString(dto);
        ValueSourceBindingDto back = mapper.readValue(json, ValueSourceBindingDto.class);

        assertEquals("u1", back.getAttributeUuid());
        assertNull(back.getAttributeName());
        assertEquals(ValueSourceType.STATIC_LIST, back.getValueSourceType());
        assertEquals("cmdb.servers", back.getCollectionRef());
        assertEquals("dc_select", back.getParams().get(0).getAttributeName());
    }

    @Test
    void bindingByNameFallbackOmitsNullUuidAndCollectionRef() throws Exception {
        ValueSourceBindingDto dto = new ValueSourceBindingDto();
        dto.setAttributeName("server");
        dto.setValueSourceType(ValueSourceType.CONNECTOR_CALLBACK);

        String json = mapper.writeValueAsString(dto);
        assertFalse(json.contains("attributeUuid"));
        assertFalse(json.contains("collectionRef"));

        ValueSourceBindingDto back = mapper.readValue(json, ValueSourceBindingDto.class);
        assertEquals("server", back.getAttributeName());
        assertEquals(ValueSourceType.CONNECTOR_CALLBACK, back.getValueSourceType());
    }
}
