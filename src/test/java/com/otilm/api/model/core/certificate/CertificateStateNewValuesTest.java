package com.otilm.api.model.core.certificate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CertificateStateNewValuesTest {

    @Test
    void pendingRegistrationValueExists() {
        assertNotNull(CertificateState.PENDING_REGISTRATION);
        assertEquals("pending_registration", CertificateState.PENDING_REGISTRATION.getCode());
        assertEquals("Pending registration", CertificateState.PENDING_REGISTRATION.getLabel());
    }

    @Test
    void registeredValueExists() {
        assertNotNull(CertificateState.REGISTERED);
        assertEquals("registered", CertificateState.REGISTERED.getCode());
        assertEquals("Registered", CertificateState.REGISTERED.getLabel());
    }

    @Test
    void findByCodeRoundTrips() {
        assertEquals(CertificateState.PENDING_REGISTRATION, CertificateState.findByCode("pending_registration"));
        assertEquals(CertificateState.REGISTERED, CertificateState.findByCode("registered"));
    }
}
