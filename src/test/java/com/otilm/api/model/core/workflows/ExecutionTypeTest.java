package com.otilm.api.model.core.workflows;

import com.otilm.api.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExecutionTypeTest {

    @ParameterizedTest
    @EnumSource(ExecutionType.class)
    void findByCodeReturnsTheType_whenGivenItsOwnCode(ExecutionType type) {
        assertEquals(type, ExecutionType.findByCode(type.getCode()));
        assertFalse(type.getLabel().isBlank());
        assertFalse(type.getDescription().isBlank());
    }

    @Test
    void exposesTheRenewCertificateType() {
        assertEquals("renewCertificate", ExecutionType.RENEW_CERTIFICATE.getCode());
        assertEquals("Renew certificate", ExecutionType.RENEW_CERTIFICATE.getLabel());
    }

    @Test
    void findByCodeRejects_whenTheCodeIsUnknown() {
        assertThrows(ValidationException.class, () -> ExecutionType.findByCode("hologram"));
    }
}
