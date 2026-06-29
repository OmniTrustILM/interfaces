package com.otilm.api.model.client.connector.v2;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeatureFlagTest {

    @Test
    void certificateRegistrationFlagExists() {
        FeatureFlag flag = FeatureFlag.CERTIFICATE_REGISTRATION;
        assertEquals("certificateRegistration", flag.getCode());
        assertEquals(List.of(ConnectorInterface.AUTHORITY), flag.getApplicableInterfaces());
    }

    @Test
    void certificateStatusPollingFlagExists() {
        FeatureFlag flag = FeatureFlag.CERTIFICATE_STATUS_POLLING;
        assertEquals("certificateStatusPolling", flag.getCode());
        assertEquals(List.of(ConnectorInterface.AUTHORITY), flag.getApplicableInterfaces());
    }

    @Test
    void findByCodeRoundTripsAllEntries() {
        for (FeatureFlag f : FeatureFlag.values()) {
            assertEquals(f, FeatureFlag.findByCode(f.getCode()));
        }
    }
}
