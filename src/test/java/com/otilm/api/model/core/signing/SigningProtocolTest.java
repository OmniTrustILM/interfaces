package com.otilm.api.model.core.signing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.ValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SigningProtocolTest {

    private final ObjectMapper mapper = new ObjectMapper();

    /** Core persists the constant names in signing_record.protocol, so renaming one costs a data migration. */
    @Test
    void persistedNamesArePinned() {
        assertEquals("CSC_API", SigningProtocol.CSC_API.name());
        assertEquals("TSP", SigningProtocol.TSP.name());
        assertEquals("INTERNAL_TSA", SigningProtocol.INTERNAL_TSA.name());
        assertEquals("ILM_NATIVE", SigningProtocol.ILM_NATIVE.name());
    }

    @Test
    void wireCodesArePinned() {
        assertEquals("csc_api", SigningProtocol.CSC_API.getCode());
        assertEquals("tsp", SigningProtocol.TSP.getCode());
        assertEquals("internal_tsa", SigningProtocol.INTERNAL_TSA.getCode());
        assertEquals("ilm_native", SigningProtocol.ILM_NATIVE.getCode());
    }

    @Test
    void findByCodeRoundTripsAllValues() {
        for (SigningProtocol protocol : SigningProtocol.values()) {
            assertEquals(protocol, SigningProtocol.findByCode(protocol.getCode()), protocol.name());
        }
    }

    /** Deliberately not a code any protocol will claim, so a future addition cannot make this pass. */
    @Test
    void unknownCodeIsRejected() {
        assertThrows(ValidationException.class, () -> SigningProtocol.findByCode("not_a_signing_protocol"));
    }

    @Test
    void serializesAsItsWireCode() throws Exception {
        assertEquals("\"internal_tsa\"", mapper.writeValueAsString(SigningProtocol.INTERNAL_TSA));
        assertEquals(SigningProtocol.INTERNAL_TSA, mapper.readValue("\"internal_tsa\"", SigningProtocol.class));
    }

    @Test
    void codesConstantsMatchTheWireCodes() {
        assertEquals(SigningProtocol.CSC_API.getCode(), SigningProtocol.Codes.CSC_API);
        assertEquals(SigningProtocol.TSP.getCode(), SigningProtocol.Codes.TSP);
        assertEquals(SigningProtocol.INTERNAL_TSA.getCode(), SigningProtocol.Codes.INTERNAL_TSA);
    }

    /** INTERNAL_TSA records issuance the platform makes on its own, so it must never be offered as enableable. */
    @Test
    void inProcessIssuanceIsNotEnableableOnAProfile() {
        assertFalse(SigningProtocol.INTERNAL_TSA.isEnableableOnProfile());
        assertFalse(SigningProtocol.enableableValues().contains(SigningProtocol.INTERNAL_TSA));
    }

    @Test
    void clientProtocolsAreEnableableOnAProfile() {
        assertTrue(SigningProtocol.TSP.isEnableableOnProfile());
        assertTrue(SigningProtocol.CSC_API.isEnableableOnProfile());
        assertEquals(List.of(SigningProtocol.CSC_API, SigningProtocol.TSP, SigningProtocol.ILM_NATIVE),
                SigningProtocol.enableableValues());
    }

    @Test
    void ilmNativeIsEnableableOnAProfile() {
        assertTrue(SigningProtocol.ILM_NATIVE.isEnableableOnProfile());
        assertEquals(SigningProtocol.ILM_NATIVE, SigningProtocol.findByCode("ilm_native"));
    }

    @Test
    void everyProtocolIsDescribed() {
        for (SigningProtocol protocol : SigningProtocol.values()) {
            assertTrue(protocol.getDescription() != null && !protocol.getDescription().isBlank(),
                    protocol.name() + " has no description");
        }
    }
}
