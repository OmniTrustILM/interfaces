package com.otilm.api.model.core.raprofile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AttributeSetMergeModeTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesEachModeAsItsCode() throws Exception {
        // given / when / then
        assertEquals("\"staticOnly\"", mapper.writeValueAsString(AttributeSetMergeMode.STATIC_ONLY));
        assertEquals("\"connectorOnly\"", mapper.writeValueAsString(AttributeSetMergeMode.CONNECTOR_ONLY));
        assertEquals("\"merge\"", mapper.writeValueAsString(AttributeSetMergeMode.MERGE));
    }

    @Test
    void deserializesEachModeFromItsCode() throws Exception {
        // given / when / then
        assertEquals(AttributeSetMergeMode.STATIC_ONLY, mapper.readValue("\"staticOnly\"", AttributeSetMergeMode.class));
        assertEquals(AttributeSetMergeMode.CONNECTOR_ONLY, mapper.readValue("\"connectorOnly\"", AttributeSetMergeMode.class));
        assertEquals(AttributeSetMergeMode.MERGE, mapper.readValue("\"merge\"", AttributeSetMergeMode.class));
    }

    @Test
    void throwsOnUnknownCode() {
        // given — a code that maps to no mode
        var unknownCode = "bogus";

        // when / then — Jackson wraps the @JsonCreator rejection; assert both the wrapper type and the originating cause
        ValueInstantiationException thrown = assertThrows(ValueInstantiationException.class,
                () -> mapper.readValue("\"" + unknownCode + "\"", AttributeSetMergeMode.class));
        assertInstanceOf(IllegalArgumentException.class, thrown.getCause());
        assertThrows(IllegalArgumentException.class, () -> AttributeSetMergeMode.fromCode(unknownCode));
    }

    @Test
    void populatesLabelAndDescriptionForEveryMode() {
        // given / when / then
        for (AttributeSetMergeMode mode : AttributeSetMergeMode.values()) {
            assertFalse(mode.getLabel().isBlank(), "label missing for " + mode.name());
            assertFalse(mode.getDescription().isBlank(), "description missing for " + mode.name());
        }
    }
}
