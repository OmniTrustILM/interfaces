package com.otilm.api.model.connector.v3.certificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CertificateOperationStatusTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void wireCodesAreCamelCase() throws Exception {
        assertEquals("\"inProgress\"", mapper.writeValueAsString(CertificateOperationStatus.IN_PROGRESS));
        assertEquals("\"completed\"", mapper.writeValueAsString(CertificateOperationStatus.COMPLETED));
        assertEquals("\"failed\"", mapper.writeValueAsString(CertificateOperationStatus.FAILED));
    }

    @Test
    void deserializesByWireCode() throws Exception {
        assertEquals(CertificateOperationStatus.IN_PROGRESS,
                mapper.readValue("\"inProgress\"", CertificateOperationStatus.class));
        assertEquals(CertificateOperationStatus.COMPLETED,
                mapper.readValue("\"completed\"", CertificateOperationStatus.class));
        assertEquals(CertificateOperationStatus.FAILED,
                mapper.readValue("\"failed\"", CertificateOperationStatus.class));
    }
}
