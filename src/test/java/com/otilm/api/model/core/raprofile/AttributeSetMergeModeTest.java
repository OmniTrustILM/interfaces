package com.otilm.api.model.core.raprofile;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AttributeSetMergeModeTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesAsCode() throws Exception {
        assertEquals("\"staticOnly\"", mapper.writeValueAsString(AttributeSetMergeMode.STATIC_ONLY));
        assertEquals("\"connectorOnly\"", mapper.writeValueAsString(AttributeSetMergeMode.CONNECTOR_ONLY));
        assertEquals("\"merge\"", mapper.writeValueAsString(AttributeSetMergeMode.MERGE));
    }

    @Test
    void deserializesFromCode() throws Exception {
        assertEquals(AttributeSetMergeMode.STATIC_ONLY, mapper.readValue("\"staticOnly\"", AttributeSetMergeMode.class));
        assertEquals(AttributeSetMergeMode.CONNECTOR_ONLY, mapper.readValue("\"connectorOnly\"", AttributeSetMergeMode.class));
        assertEquals(AttributeSetMergeMode.MERGE, mapper.readValue("\"merge\"", AttributeSetMergeMode.class));
    }

    @Test
    void unknownCodeThrows() {
        assertThrows(Exception.class, () -> mapper.readValue("\"bogus\"", AttributeSetMergeMode.class));
        assertThrows(IllegalArgumentException.class, () -> AttributeSetMergeMode.fromCode("bogus"));
    }

    @Test
    void labelsAndDescriptionsArePopulated() {
        for (AttributeSetMergeMode mode : AttributeSetMergeMode.values()) {
            assertFalse(mode.getLabel().isBlank(), "label missing for " + mode.name());
            assertFalse(mode.getDescription().isBlank(), "description missing for " + mode.name());
        }
    }
}
