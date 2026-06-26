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
    void roundTripsBindingByUuid() throws Exception {
        // given — a binding identified by attributeUuid, with no attributeName
        var boundUuid = "u1";
        var collectionRef = "cmdb.servers";
        var paramName = "dc_select";
        var param = new SourceParam();
        param.setAttributeName(paramName);
        var dto = new ValueSourceBindingDto();
        dto.setAttributeUuid(boundUuid);
        dto.setValueSourceType(ValueSourceType.STATIC_LIST);
        dto.setCollectionRef(collectionRef);
        dto.setParams(List.of(param));

        // when
        var json = mapper.writeValueAsString(dto);
        ValueSourceBindingDto back = mapper.readValue(json, ValueSourceBindingDto.class);

        // then
        assertEquals(boundUuid, back.getAttributeUuid());
        assertNull(back.getAttributeName());
        assertEquals(ValueSourceType.STATIC_LIST, back.getValueSourceType());
        assertEquals(collectionRef, back.getCollectionRef());
        assertEquals(paramName, back.getParams().get(0).getAttributeName());
    }

    @Test
    void omitsNullUuidAndCollectionRef_whenBindingByName() throws Exception {
        // given — a binding identified by attributeName, with no uuid or collectionRef
        var boundName = "server";
        var dto = new ValueSourceBindingDto();
        dto.setAttributeName(boundName);
        dto.setValueSourceType(ValueSourceType.CONNECTOR_CALLBACK);

        // when
        var json = mapper.writeValueAsString(dto);
        ValueSourceBindingDto back = mapper.readValue(json, ValueSourceBindingDto.class);

        // then
        assertFalse(json.contains("attributeUuid"));
        assertFalse(json.contains("collectionRef"));
        assertEquals(boundName, back.getAttributeName());
        assertEquals(ValueSourceType.CONNECTOR_CALLBACK, back.getValueSourceType());
    }
}
