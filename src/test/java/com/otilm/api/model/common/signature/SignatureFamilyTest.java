package com.otilm.api.model.common.signature;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.ValidationException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SignatureFamilyTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void wireCodesArePinned() {
        assertEquals("pades", SignatureFamily.PADES.code());
        assertEquals("xades", SignatureFamily.XADES.code());
        assertEquals("cades", SignatureFamily.CADES.code());
        assertEquals("jades", SignatureFamily.JADES.code());
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

    @ParameterizedTest
    @MethodSource("constants")
    void deserializesToTheConstantOfTheSameCode(SignatureFamily constant) throws Exception {
        String wireCode = mapper.writeValueAsString(constant.code());
        assertEquals(constant, mapper.readValue(wireCode, SignatureFamily.class));
    }

    @Test
    void familiesWithTheSameCodeAreEqual() {
        assertEquals(SignatureFamily.PADES, new SignatureFamily("pades"));
    }

    /** Any well-formed code names a family. A connector may declare one that interfaces does not type. */
    @Test
    void acceptsAFamilyOnlyAConnectorDeclares() {
        assertEquals("mrtd", new SignatureFamily("mrtd").code());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"PAdES", "1pades", "pa des", "pades_b", "-pades", "pkcs#7"})
    void malformedCodeIsRejected(String malformed) {
        assertThrows(ValidationException.class, () -> new SignatureFamily(malformed));
    }

    @Test
    void serializesAsItsWireCode() throws Exception {
        assertEquals("\"cades\"", mapper.writeValueAsString(SignatureFamily.CADES));
        assertEquals(SignatureFamily.CADES, mapper.readValue("\"cades\"", SignatureFamily.class));
    }

    private static Stream<SignatureFamily> constants() {
        return Stream.of(SignatureFamily.PADES, SignatureFamily.XADES, SignatureFamily.CADES, SignatureFamily.JADES);
    }
}
