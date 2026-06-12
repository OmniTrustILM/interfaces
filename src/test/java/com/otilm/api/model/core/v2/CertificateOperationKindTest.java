package com.otilm.api.model.core.v2;

import com.otilm.api.exception.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CertificateOperationKindTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesToUppercaseWireCode() throws Exception {
        assertEquals("\"ISSUE\"", mapper.writeValueAsString(CertificateOperationKind.ISSUE));
        assertEquals("\"REGISTER\"", mapper.writeValueAsString(CertificateOperationKind.REGISTER));
    }

    @Test
    void deserializesFromWireCode() throws Exception {
        assertSame(CertificateOperationKind.REVOKE,
                mapper.readValue("\"REVOKE\"", CertificateOperationKind.class));
    }

    @Test
    void findByCodeResolvesEveryEntry() {
        for (CertificateOperationKind kind : CertificateOperationKind.values()) {
            assertSame(kind, CertificateOperationKind.findByCode(kind.getCode()));
        }
    }

    @Test
    void findByCodeRejectsUnknown() {
        assertThrows(ValidationException.class, () -> CertificateOperationKind.findByCode("issue"));
    }
}
