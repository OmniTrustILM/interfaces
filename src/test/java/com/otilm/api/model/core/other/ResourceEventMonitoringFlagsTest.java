package com.otilm.api.model.core.other;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceEventMonitoringFlagsTest {

    @Test
    void notCompliantIsMonitoringAndNotifiesOnceWithoutReminders() {
        assertTrue(ResourceEvent.CERTIFICATE_NOT_COMPLIANT.isMonitoring());
        assertTrue(ResourceEvent.CERTIFICATE_NOT_COMPLIANT.isSingleNotificationWithoutReminders());
    }

    @Test
    void expiringIsMonitoringButNotSingleNotification() {
        assertTrue(ResourceEvent.CERTIFICATE_EXPIRING.isMonitoring());
        assertFalse(ResourceEvent.CERTIFICATE_EXPIRING.isSingleNotificationWithoutReminders());
    }

    @Test
    void statusChangedIsNeitherMonitoringNorSingleNotification() {
        assertFalse(ResourceEvent.CERTIFICATE_STATUS_CHANGED.isMonitoring());
        assertFalse(ResourceEvent.CERTIFICATE_STATUS_CHANGED.isSingleNotificationWithoutReminders());
    }

    @Test
    void renewalDueIsMonitoringButNotSingleNotification() {
        assertTrue(ResourceEvent.CERTIFICATE_RENEWAL_DUE.isMonitoring());
        assertFalse(ResourceEvent.CERTIFICATE_RENEWAL_DUE.isSingleNotificationWithoutReminders());
    }
}
