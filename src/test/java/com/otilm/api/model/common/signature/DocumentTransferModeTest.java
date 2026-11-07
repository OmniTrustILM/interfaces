package com.otilm.api.model.common.signature;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentTransferModeTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void wireCodesArePinned() {
        assertEquals("inline", DocumentTransferMode.INLINE.getCode());
        assertEquals("digestOnly", DocumentTransferMode.DIGEST_ONLY.getCode());
    }

    @Test
    void findByCodeRoundTripsAllValues() {
        for (DocumentTransferMode mode : DocumentTransferMode.values()) {
            assertEquals(mode, DocumentTransferMode.findByCode(mode.getCode()), mode.name());
        }
    }

    @Test
    void unknownCodeIsRejected() {
        assertThrows(ValidationException.class, () -> DocumentTransferMode.findByCode("detached"));
    }

    @Test
    void serializesAsItsWireCode() throws Exception {
        assertEquals("\"digestOnly\"", mapper.writeValueAsString(DocumentTransferMode.DIGEST_ONLY));
        assertEquals(DocumentTransferMode.DIGEST_ONLY, mapper.readValue("\"digestOnly\"", DocumentTransferMode.class));
    }

    /**
     * The constants feed annotations that need compile-time literals, so the codes are declared twice over. Asserting
     * the literal is what stops the two copies drifting.
     */
    @Test
    void codesConstantsMatchTheWireCodes() {
        assertEquals("inline", DocumentTransferMode.Codes.INLINE);
        assertEquals("digestOnly", DocumentTransferMode.Codes.DIGEST_ONLY);
    }

    @Test
    void everyModeIsLabelledAndDescribed() {
        for (DocumentTransferMode mode : DocumentTransferMode.values()) {
            assertTrue(mode.getLabel() != null && !mode.getLabel().isBlank(), mode.name() + " has no label");
            assertTrue(mode.getDescription() != null && !mode.getDescription().isBlank(),
                    mode.name() + " has no description");
        }
    }
}
