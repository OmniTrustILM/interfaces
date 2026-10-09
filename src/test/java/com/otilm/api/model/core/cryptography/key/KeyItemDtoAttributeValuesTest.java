package com.otilm.api.model.core.cryptography.key;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.core.search.FilterFieldSource;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class KeyItemDtoAttributeValuesTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void omitsAttributeValuesWhenNoColumnsWereProjected() throws Exception {
        // given — a key listing entry as it is built when the request carried no columns
        var dto = new KeyItemDto();
        dto.setName("signing-key");

        // when
        var json = mapper.writeValueAsString(dto);

        // then
        assertFalse(json.contains("attributeValues"));
        assertNull(dto.getAttributeValues());
    }

    @Test
    void roundTripsValuesKeyedBySourceThenIdentifier() throws Exception {
        // given
        var dto = new KeyItemDto();
        dto
                .setAttributeValues(Map
                        .of(FilterFieldSource.CUSTOM,
                                Map.of("purpose", List.of(new StringAttributeContentV3("signing")))));

        // when
        var back = mapper.readValue(mapper.writeValueAsString(dto), KeyItemDto.class);

        // then
        assertEquals("signing",
                back.getAttributeValues().get(FilterFieldSource.CUSTOM).get("purpose").get(0).getData());
    }
}
