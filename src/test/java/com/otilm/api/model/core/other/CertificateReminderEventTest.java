package com.otilm.api.model.core.other;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateReminderEventTest {

    @Test
    void notCompliantRemindsAndNotifiesOnceWhenProfileSetsNoReminders() {
        assertTrue(ResourceEvent.CERTIFICATE_NOT_COMPLIANT.isMonitoring());
        assertTrue(ResourceEvent.CERTIFICATE_NOT_COMPLIANT.isSingleNotificationWithoutReminders());
    }

    @Test
    void expiringKeepsItsMonitoringBehaviour() {
        assertTrue(ResourceEvent.CERTIFICATE_EXPIRING.isMonitoring());
        assertFalse(ResourceEvent.CERTIFICATE_EXPIRING.isSingleNotificationWithoutReminders());
    }

    @Test
    void otherEventsAreNotAffected() {
        assertFalse(ResourceEvent.CERTIFICATE_STATUS_CHANGED.isMonitoring());
        assertFalse(ResourceEvent.CERTIFICATE_STATUS_CHANGED.isSingleNotificationWithoutReminders());
    }
}
