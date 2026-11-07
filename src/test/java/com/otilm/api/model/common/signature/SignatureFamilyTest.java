package com.otilm.api.model.common.signature;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.connector.v2.ConnectorInterface;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignatureFamilyTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void wireCodesArePinned() {
        assertEquals("pades", SignatureFamily.PADES.getCode());
        assertEquals("xades", SignatureFamily.XADES.getCode());
        assertEquals("cades", SignatureFamily.CADES.getCode());
        assertEquals("jades", SignatureFamily.JADES.getCode());
    }

    @Test
    void findByCodeRoundTripsAllValues() {
        for (SignatureFamily family : SignatureFamily.values()) {
            assertEquals(family, SignatureFamily.findByCode(family.getCode()), family.name());
        }
    }

    @Test
    void unknownCodeIsRejected() {
        assertThrows(ValidationException.class, () -> SignatureFamily.findByCode("pkcs7"));
    }

    @Test
    void serializesAsItsWireCode() throws Exception {
        assertEquals("\"cades\"", mapper.writeValueAsString(SignatureFamily.CADES));
        assertEquals(SignatureFamily.CADES, mapper.readValue("\"cades\"", SignatureFamily.class));
    }

    /**
     * The constants feed the {@code @JsonSubTypes} annotations, which need compile-time literals, so the codes are
     * declared twice over. Asserting the literal is what stops the two copies drifting.
     */
    @Test
    void codesConstantsMatchTheWireCodes() {
        assertEquals("pades", SignatureFamily.Codes.PADES);
        assertEquals("xades", SignatureFamily.Codes.XADES);
        assertEquals("cades", SignatureFamily.Codes.CADES);
        assertEquals("jades", SignatureFamily.Codes.JADES);
    }

    /** Labels are what an operator picks a family by in the UI, so each must read as the family's proper name. */
    @Test
    void labelsAreTheFamilyNames() {
        assertEquals("PAdES", SignatureFamily.PADES.getLabel());
        assertEquals("XAdES", SignatureFamily.XADES.getLabel());
        assertEquals("CAdES", SignatureFamily.CADES.getLabel());
        assertEquals("JAdES", SignatureFamily.JADES.getLabel());
    }

    @Test
    void everyFamilyIsDescribed() {
        for (SignatureFamily family : SignatureFamily.values()) {
            assertTrue(family.getDescription() != null && !family.getDescription().isBlank(),
                    family.name() + " has no description");
        }
    }

    /** An operator reads the level name beside the rung. */
    @Test
    void everyFamilyNamesEveryLevelInEtsiBaselineTerms() {
        assertEquals("PAdES-B-B", SignatureFamily.PADES.getLevelName(SignatureLevel.SIGNED));
        assertEquals("PAdES-B-T", SignatureFamily.PADES.getLevelName(SignatureLevel.TIMESTAMPED));
        assertEquals("PAdES-B-LT", SignatureFamily.PADES.getLevelName(SignatureLevel.LONG_TERM));
        assertEquals("PAdES-B-LTA", SignatureFamily.PADES.getLevelName(SignatureLevel.ARCHIVAL));
        assertEquals("CAdES-B-LT", SignatureFamily.CADES.getLevelName(SignatureLevel.LONG_TERM));
        assertEquals("XAdES-B-T", SignatureFamily.XADES.getLevelName(SignatureLevel.TIMESTAMPED));
        assertEquals("JAdES-B-LTA", SignatureFamily.JADES.getLevelName(SignatureLevel.ARCHIVAL));
    }

    /** Core refuses a document sent in a mode its family cannot sign from. */
    @Test
    void onlyPadesRefusesADigestOnlyDocument() {
        assertFalse(SignatureFamily.PADES.accepts(DocumentTransferMode.DIGEST_ONLY));
        assertTrue(SignatureFamily.XADES.accepts(DocumentTransferMode.DIGEST_ONLY));
        assertTrue(SignatureFamily.CADES.accepts(DocumentTransferMode.DIGEST_ONLY));
        assertTrue(SignatureFamily.JADES.accepts(DocumentTransferMode.DIGEST_ONLY));
    }

    @ParameterizedTest
    @EnumSource(SignatureFamily.class)
    void everyFamilyAcceptsAnInlineDocument(SignatureFamily family) {
        assertTrue(family.accepts(DocumentTransferMode.INLINE));
    }

    @Test
    void namingAnAbsentLevelIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> SignatureFamily.PADES.getLevelName(null));
    }

    /**
     * Each family is advertised by its own connector interface code, which is what lets an image serve one family and
     * reject the rest. A family with no code could never be advertised at all.
     */
    @Test
    void everyFamilyHasAConnectorInterfaceCode() {
        Map<SignatureFamily, ConnectorInterface> expected = Map
                .of(SignatureFamily.PADES, ConnectorInterface.PADES_FORMATTING, SignatureFamily.XADES,
                        ConnectorInterface.XADES_FORMATTING, SignatureFamily.CADES, ConnectorInterface.CADES_FORMATTING,
                        SignatureFamily.JADES, ConnectorInterface.JADES_FORMATTING);

        assertEquals(List.of(SignatureFamily.values()).size(), expected.size(),
                "a family was added without a connector interface code");
        expected
                .forEach((family, code) -> assertEquals(family.getCode() + "Formatting", code.getCode(),
                        family.name() + " and " + code.name() + " disagree on the wire name"));
    }
}
