package com.otilm.api.model.common.signature;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SignatureFamilyTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void familiesWithTheSameCodeAreEqual() {
        assertEquals(new SignatureFamily("pades"), new SignatureFamily("pades"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"pades", "mrtd", "cose-sign1"})
    void anyWellFormedCodeNamesAFamily(String code) {
        assertEquals(code, new SignatureFamily(code).code());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"PAdES", "1pades", "pa des", "pades_b", "-pades", "pkcs#7"})
    void malformedCodeIsRejected(String malformed) {
        assertThrows(ValidationException.class, () -> new SignatureFamily(malformed));
    }

    @Test
    void serializesAsItsWireCode() throws Exception {
        SignatureFamily cades = new SignatureFamily("cades");

        assertEquals("\"cades\"", mapper.writeValueAsString(cades));
        assertEquals(cades, mapper.readValue("\"cades\"", SignatureFamily.class));
    }
}
