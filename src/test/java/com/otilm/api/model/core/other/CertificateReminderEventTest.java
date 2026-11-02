package com.otilm.api.model.core.other;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateReminderEventTest {

    @Test
    void notCompliantIsAMonitoringEvent() {
        assertTrue(ResourceEvent.CERTIFICATE_NOT_COMPLIANT.isMonitoring());
    }

    @Test
    void otherCertificateEventsKeepTheirMonitoringFlag() {
        assertTrue(ResourceEvent.CERTIFICATE_EXPIRING.isMonitoring());
        assertFalse(ResourceEvent.CERTIFICATE_STATUS_CHANGED.isMonitoring());
    }
}
