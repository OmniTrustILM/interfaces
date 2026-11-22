package com.otilm.api.model.common.signature;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.ValidationException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignatureLevelTest {

    private final ObjectMapper mapper = new ObjectMapper();

    /** The codes are persisted and travel on the wire, so a changed one silently reinterprets stored profiles. */
    @Test
    void wireCodesArePinned() {
        assertEquals("signed", SignatureLevel.SIGNED.getCode());
        assertEquals("timestamped", SignatureLevel.TIMESTAMPED.getCode());
        assertEquals("long_term", SignatureLevel.LONG_TERM.getCode());
        assertEquals("archival", SignatureLevel.ARCHIVAL.getCode());
    }

    /** The ladder is compared by ordinal, so the declaration order is contract rather than presentation. */
    @Test
    void levelsAreDeclaredLowestToHighest() {
        assertArrayOrder(SignatureLevel.SIGNED, SignatureLevel.TIMESTAMPED, SignatureLevel.LONG_TERM,
                SignatureLevel.ARCHIVAL);
    }

    @Test
    void aLevelIsWithinItself() {
        for (SignatureLevel level : SignatureLevel.values()) {
            assertTrue(level.isWithin(level), level.name());
        }
    }

    @Test
    void aLowerLevelIsWithinAHigherCeiling() {
        assertTrue(SignatureLevel.SIGNED.isWithin(SignatureLevel.ARCHIVAL));
        assertTrue(SignatureLevel.LONG_TERM.isWithin(SignatureLevel.ARCHIVAL));
    }

    @Test
    void aHigherLevelIsNotWithinALowerCeiling() {
        assertFalse(SignatureLevel.ARCHIVAL.isWithin(SignatureLevel.SIGNED));
        assertFalse(SignatureLevel.LONG_TERM.isWithin(SignatureLevel.TIMESTAMPED));
    }

    /** An absent ceiling is no ceiling at all, so nothing may pass it rather than everything. */
    @Test
    void nothingIsWithinAnAbsentCeiling() {
        assertFalse(SignatureLevel.SIGNED.isWithin(null));
    }

    @Test
    void findByCodeRoundTripsAllValues() {
        for (SignatureLevel level : SignatureLevel.values()) {
            assertEquals(level, SignatureLevel.findByCode(level.getCode()), level.name());
        }
    }

    @Test
    void unknownCodeIsRejected() {
        assertThrows(ValidationException.class, () -> SignatureLevel.findByCode("lta"));
    }

    @Test
    void codesAreDistinct() {
        Set<String> codes = Arrays
                .stream(SignatureLevel.values())
                .map(SignatureLevel::getCode)
                .collect(Collectors.toSet());

        assertEquals(SignatureLevel.values().length, codes.size());
    }

    @Test
    void serializesAsItsWireCode() throws Exception {
        assertEquals("\"long_term\"", mapper.writeValueAsString(SignatureLevel.LONG_TERM));
        assertEquals(SignatureLevel.LONG_TERM, mapper.readValue("\"long_term\"", SignatureLevel.class));
    }

    @Test
    void everyLevelIsLabelledAndDescribed() {
        for (SignatureLevel level : SignatureLevel.values()) {
            assertTrue(level.getLabel() != null && !level.getLabel().isBlank(), level.name() + " has no label");
            assertTrue(level.getDescription() != null && !level.getDescription().isBlank(),
                    level.name() + " has no description");
        }
    }

    private static void assertArrayOrder(SignatureLevel... expected) {
        assertEquals(Arrays.asList(expected), Arrays.asList(SignatureLevel.values()));
    }
}
