package com.czertainly.api.model.connector.v2;

import com.czertainly.api.exception.ValidationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CertificateOperationStatusTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializesToJsonValueCode() throws Exception {
        assertEquals("\"inProgress\"", mapper.writeValueAsString(CertificateOperationStatus.IN_PROGRESS));
        assertEquals("\"completed\"", mapper.writeValueAsString(CertificateOperationStatus.COMPLETED));
        assertEquals("\"failed\"", mapper.writeValueAsString(CertificateOperationStatus.FAILED));
    }

    @Test
    void deserializesFromJsonValueCode() throws Exception {
        assertEquals(CertificateOperationStatus.IN_PROGRESS,
                mapper.readValue("\"inProgress\"", CertificateOperationStatus.class));
        assertEquals(CertificateOperationStatus.COMPLETED,
                mapper.readValue("\"completed\"", CertificateOperationStatus.class));
        assertEquals(CertificateOperationStatus.FAILED,
                mapper.readValue("\"failed\"", CertificateOperationStatus.class));
    }

    @Test
    void unknownCodeThrowsValidationException() {
        // Deserializing an unknown code surfaces a Jackson mapping error whose root
        // cause carries the validation failure raised by the enum factory method.
        // The assertion walks the cause chain to verify both layers.
        JsonMappingException ex = assertThrows(JsonMappingException.class,
                () -> mapper.readValue("\"bogus\"", CertificateOperationStatus.class));
        assertInstanceOf(ValidationException.class, ex.getCause(),
                "expected the underlying cause to be ValidationException, got: " + ex.getCause());
    }

    @Test
    void findByCodeRejectsUnknownDirectly() {
        assertThrows(ValidationException.class,
                () -> CertificateOperationStatus.findByCode("bogus"));
    }

    @Test
    void labelsAndDescriptionsArePopulated() {
        for (CertificateOperationStatus status : CertificateOperationStatus.values()) {
            assertEquals(false, status.getLabel().isBlank(),
                    "label missing for " + status.name());
            assertEquals(false, status.getDescription().isBlank(),
                    "description missing for " + status.name());
        }
    }
}
