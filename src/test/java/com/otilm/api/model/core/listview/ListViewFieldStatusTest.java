package com.otilm.api.model.core.listview;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ListViewFieldStatusTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void travelsByItsCode() throws Exception {
        assertEquals("\"available\"", mapper.writeValueAsString(ListViewFieldStatus.AVAILABLE));
        assertEquals("\"unavailable\"", mapper.writeValueAsString(ListViewFieldStatus.UNAVAILABLE));
        assertEquals("\"replaced\"", mapper.writeValueAsString(ListViewFieldStatus.REPLACED));
        assertEquals(ListViewFieldStatus.REPLACED, mapper.readValue("\"replaced\"", ListViewFieldStatus.class));
    }

    @Test
    void refusesAnUnknownCode() {
        assertThrows(IllegalArgumentException.class, () -> ListViewFieldStatus.fromCode("gone"));
    }
}
